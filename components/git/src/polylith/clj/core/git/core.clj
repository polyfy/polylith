(ns polylith.clj.core.git.core
  (:require [clojure.string :as str]
            [polylith.clj.core.git.tag :as tag]
            [polylith.clj.core.shell.interface :as shell]))

(def branch "master")
(def repo "https://github.com/polyfy/polylith.git")

(defn is-git-repo? [ws-dir]
  (let [{:keys [exit]} (shell/sh-with-return "git" "rev-parse" "--is-inside-work-tree" :dir ws-dir)]
    (and (zero? exit)
         (= "true" (first (str/split-lines (shell/sh-ignore-exception "git" "rev-parse" "--is-inside-work-tree" :dir ws-dir)))))))

(defn init [ws-dir git-repo? branch]
  (try
    (when (not git-repo?)
      (shell/sh "git" "init" :dir ws-dir)
      (shell/sh "git" "checkout" "-b" (or branch "main") :dir ws-dir))
    (shell/sh "git" "add" "." :dir ws-dir)
    (shell/sh "git" "commit" "-m" "Workspace created." :dir ws-dir)
    (catch Exception e
      (println (str "Cannot create a git repository for the workspace.\n"
                    "Please try to create it manually instead: " (.getMessage e))))))

(defn add [ws-dir filename is-git-add]
  (when is-git-add
    (shell/sh "git" "add" filename :dir ws-dir)))

(defn current-branch []
  (try
    (str/trim-newline (shell/sh "git" "rev-parse" "--abbrev-ref" "HEAD"))
    (catch Exception _
      "master")))

(defn latest-polylith-sha [branch]
  (some-> (shell/sh-ignore-exception "git" "ls-remote" repo (str "refs/heads/" branch))
          (str/split #"\t")
          first))

(defn diff-command-parts [sha1 sha2]
  (if sha1
    (if sha2
      ["git" "diff" sha1 sha2 "--name-only"]
      ["git" "diff" sha1 "--name-only"])
    ["git" "diff" "--name-only"]))

(defn diff-command [sha1 sha2]
  (str/join " " (diff-command-parts sha1 sha2)))

(defn keep-path? [path prefix]
  (if (= "" prefix)
    true
    (str/starts-with? path prefix)))

(defn remove-prefix
  "If the workspace lives inside a git repository (not at its root)
   then we need to remove the workspace name/path from the path."
  [path prefix]
  (if (str/starts-with? path prefix)
    (subs path (count prefix))
    path))

(defn diff
  "ws-local-dir is the name of the workspace if it lives within a git repo,
   otherwise nil. If :no-changes is passed in, return an empty vector."
  [ws-dir ws-local-dir is-no-changes sha1 sha2]
  (if is-no-changes
    []
    (let [{:keys [exit out err]} (apply shell/sh-with-return (concat (diff-command-parts sha1 sha2) [:dir ws-dir]))
          prefix (if ws-local-dir
                   (str ws-local-dir "/")
                   "")]
      (if (zero? exit)
        (mapv #(remove-prefix % prefix)
              (filter #(keep-path? % prefix)
                      (str/split-lines out)))
        (do
          (println err)
          [])))))

(defn first-committed-sha [ws-dir]
  (last (str/split-lines (shell/sh "git" "log" "--format=%H" :dir ws-dir))))

(defn root-dir []
  (try
    [true (str/trim-newline (shell/sh "git" "rev-parse" "--show-toplevel"))]
    (catch Exception e
      [false (.getMessage e)])))

(defn drop-or-keep [elements drop?]
  (when elements (if drop? (rest elements) elements)))

(defn extract-since [since]
  (if (str/starts-with? since "previous-")
    [(subs since 9) true]
    [since false]))

(defn sha [ws-dir since tag-patterns]
  (let [[since-tag previous?] (extract-since (or since "stable"))
        tag-pattern (get-in tag-patterns [(keyword since-tag)])]
    (if tag-pattern
      (or (-> (tag/matching-tags ws-dir tag-pattern)
              (drop-or-keep previous?)
              first)
          {:sha (first-committed-sha ws-dir)})
      {:sha since})))

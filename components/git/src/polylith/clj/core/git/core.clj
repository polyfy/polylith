(ns ^:no-doc polylith.clj.core.git.core
  (:require [clojure.string :as str]
            [polylith.clj.core.git.tag :as tag]
            [polylith.clj.core.sh.interface :as sh]))

(def branch "master")
(def repo "https://github.com/polyfy/polylith.git")

(defn is-git-repo? [ws-dir]
  (let [{:keys [out exit]} (sh/execute-with-return "git" "rev-parse" "--is-inside-work-tree" :dir ws-dir)]
    (and (zero? exit)
         (= "true" (first (str/split-lines out))))))

(defn is-polylith-repo? [ws-dir]
  (let [{:keys [out exit]} (sh/execute-with-return "git" "config" "--get" "remote.origin.url" :dir ws-dir)]
    (and (zero? exit)
         (= "git@github.com:polyfy/polylith.git"
            (first (str/split-lines out))))))

(defn init [ws-dir git-repo? branch]
  (try
    (when (not git-repo?)
      (sh/execute "git" "init" :dir ws-dir)
      (sh/execute "git" "checkout" "-b" (or branch "main") :dir ws-dir))
    (sh/execute "git" "add" "." :dir ws-dir)
    (sh/execute "git" "commit" "-m" "Workspace created." :dir ws-dir)
    (catch Exception e
      (println (str "Cannot create a git repository for the workspace.\n"
                    "Please try to create it manually instead: " (.getMessage e))))))

(defn add [ws-dir filename git-add?]
  (when git-add?
    (sh/execute "git" "add" filename :dir ws-dir)))

(defn current-branch []
  (try
    (str/trim-newline (sh/execute "git" "rev-parse" "--abbrev-ref" "HEAD"))
    (catch Exception _
      "master")))

(defn latest-polylith-sha [branch]
  (some-> (sh/execute-ignore-exception "git" "ls-remote" repo (str "refs/heads/" branch))
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
  [ws-dir ws-local-dir is-no-changes changed-files sha1 sha2]
  (cond (seq changed-files) changed-files
        is-no-changes []
        :else (let [{:keys [exit out err]} (apply sh/execute-with-return (concat (diff-command-parts sha1 sha2) [:dir ws-dir]))
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
  (last (str/split-lines (sh/execute "git" "log" "--format=%H" :dir ws-dir))))

(defn root-dir []
  (try
    [true (str/trim-newline (sh/execute "git" "rev-parse" "--show-toplevel"))]
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

(ns polylith.clj.core.git.core
  (:require [clojure.string :as str]
            [polylith.clj.core.git.tag :as tag]
            [polylith.clj.core.shell.interface :as shell]))

(def repo "https://github.com/polyfy/polylith.git")

(defn is-git-repo? [ws-dir]
  (try
    (= "true" (first (str/split-lines (shell/sh "git" "rev-parse" "--is-inside-work-tree" :dir ws-dir))))
    (catch Exception _
      false)))

(defn init [ws-dir]
  (try
    (shell/sh "git" "init" :dir ws-dir)
    (shell/sh "git" "add" "." :dir ws-dir)
    (shell/sh "git" "commit" "-m" "Workspace created." :dir ws-dir)
    (catch Exception e
      (println (str "Cannot create a git repository for the workspace.\n"
                    "Please try to create it manually instead: " (.getMessage e))))))

(defn add [ws-dir filename]
  (shell/sh "git" "add" filename :dir ws-dir))

(defn current-branch []
  (try
    (str/trim-newline (shell/sh "git" "rev-parse" "--abbrev-ref" "HEAD"))
    (catch Exception _
      "master")))

(defn latest-polylith-sha [branch]
  (-> (shell/sh "git" "ls-remote" repo
                (str "refs/heads/" branch))
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

(defn remove-prefix
  "If the workspace lives inside a git repository (not at its root)
   then we need to remove the workspace name/path from the path."
  [path prefix]
  (if (str/starts-with? path prefix)
    (subs path (count prefix))
    path))

(defn diff
  "ws-local-dir is the name of the workspace if it lives within a git repo,
   otherwise nil."
  [ws-dir ws-local-dir sha1 sha2]
  (let [files (apply shell/sh (concat (diff-command-parts sha1 sha2) [:dir ws-dir]))
        prefix (if ws-local-dir
                 (str ws-local-dir "/")
                 "")]
    (mapv #(remove-prefix % prefix)
          (str/split-lines files))))

(defn first-committed-sha [ws-dir]
  (last (str/split-lines (shell/sh "git" "log" "--format=%H" :dir ws-dir))))

(defn root-dir []
  (try
    [true (str/trim-newline (shell/sh "git" "rev-parse" "--show-toplevel"))]
    (catch Exception e
      [false (.getMessage e)])))

(defn drop-or-keep [elements drop?]
  (when elements (if drop? (rest elements) elements)))

(defn release [ws-dir pattern previous?]
  (or (-> (tag/matching-tags ws-dir pattern)
          (drop-or-keep previous?)
          first)
    {:sha (first-committed-sha ws-dir)}))

(defn latest-stable [ws-dir pattern]
  (or (first (tag/matching-tags ws-dir pattern))
      {:sha (first-committed-sha ws-dir)}))

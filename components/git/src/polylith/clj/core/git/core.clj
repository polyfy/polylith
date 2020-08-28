(ns polylith.clj.core.git.core
  (:require [clojure.string :as str]
            [polylith.clj.core.shell.interfc :as shell]))

(defn is-git-repo? [ws-dir]
  (try
    (= "true" (first (str/split-lines (shell/sh "git" "rev-parse" "--is-inside-work-tree" :dir ws-dir))))
    (catch Exception _
      false)))

(defn init [ws-dir]
  (try
    (shell/sh "git" "init" :dir ws-dir)
    (shell/sh "git" "add" "." :dir ws-dir)
    (shell/sh "git" "commit" "-m" "Initial commit." :dir ws-dir)
    (catch Exception _
      (println (str "Cannot create a git repository for the workspace.\n"
                    "Please try to create it manually instead.")))))

(defn add [ws-dir filename]
  (shell/sh "git" "add" filename :dir ws-dir))

(defn diff-command-parts [sha1 sha2]
  (if sha1
    (if sha2
      ["git" "diff" sha1 sha2 "--name-only"]
      ["git" "diff" sha1 "--name-only"])
    ["git" "diff" "--name-only"]))

(defn diff-command [sha1 sha2]
  (str/join " " (diff-command-parts sha1 sha2)))

(defn diff [ws-dir sha1 sha2]
  (let [files (apply shell/sh (concat (diff-command-parts sha1 sha2)
                                      [:dir ws-dir]))]
    (str/split-lines files)))

(defn list-tags [ws-dir pattern]
  (let [sort (str "--sort=committerdate")]
    (filterv #(-> % str/blank? not)
             (str/split-lines (shell/sh "git" "tag" sort "-l" pattern :dir ws-dir)))))

(defn sha-of-tag [ws-dir tag-name]
  (first (str/split-lines (shell/sh "git" "rev-list" "-1" tag-name :dir ws-dir))))

(defn first-commited-sha [ws-dir]
  (last (str/split-lines (shell/sh "git" "log" "--format=%H" :dir ws-dir))))

(defn latest-stable [ws-dir pattern]
  (if-let [tag-name (last (list-tags ws-dir pattern))]
    {:tag tag-name
     :sha (sha-of-tag ws-dir tag-name)}
    {:sha (first-commited-sha ws-dir)}))

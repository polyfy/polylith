(ns polylith.clj.core.git.core
  (:require [clojure.string :as str]
            [polylith.clj.core.shell.interfc :as shell]))

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

(defn current-sha [ws-dir]
  (try
    (str/trim (shell/sh "git" "rev-parse" "HEAD" :dir ws-dir))
    (catch Exception _
      (println (str "Couldn't get current SHA")))))

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
    (str/split files #"\n")))

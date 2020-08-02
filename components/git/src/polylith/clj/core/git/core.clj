(ns polylith.clj.core.git.core
  (:require [clojure.string :as str]
            [polylith.clj.core.shell.interfc :as shell]))

(defn init [ws-path]
  (try
    (shell/sh "git" "init" :dir ws-path)
    (shell/sh "git" "add" "." :dir ws-path)
    (shell/sh "git" "commit" "-m" "Initial commit." :dir ws-path)
    (catch Exception _
      (println (str "Cannot create a git repository for the workspace.\n"
                    "Please try to create it manually instead.")))))

(defn add [ws-path filename]
  (shell/sh "git" "add" filename :dir ws-path))

(defn current-sha [ws-path]
  (try
    (str/trim (shell/sh "git" "rev-parse" "HEAD" :dir ws-path))
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

(defn diff [ws-path sha1 sha2]
  (let [files (apply shell/sh (concat (diff-command-parts sha1 sha2)
                                      [:dir ws-path]))]
    (str/split files #"\n")))

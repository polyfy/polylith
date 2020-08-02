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

(defn diff [sha1 sha2]
  (let [ws-path "."
        sha? (or sha1 sha2)
        sha-1 (when sha? (or sha1 "HEAD"))
        sha-2 (when sha? (or sha2 "HEAD"))
        files (if sha?
                (shell/sh "git" "diff" sha-1 sha-2 "--name-only" :dir ws-path)
                (shell/sh "git" "diff" "--name-only" :dir ws-path))]
    (str/split files #"\n")))

(defn diff-command [sha1 sha2]
  (let [sha? (or sha1 sha2)
        sha-1 (when sha? (or sha1 "HEAD"))
        sha-2 (when sha? (or sha2 "HEAD"))]
    (if sha?
      (str "git diff " sha-1 " " sha-2 " --name-only")
      (str "git diff --name-only"))))

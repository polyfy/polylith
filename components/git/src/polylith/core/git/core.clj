(ns polylith.core.git.core
  (:require [clojure.string :as str]
            [polylith.core.shell.interface :as shell]))

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

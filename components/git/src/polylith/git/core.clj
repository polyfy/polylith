(ns polylith.git.core
  (:require [clojure.string :as str]
            [polylith.shell.interface :as shell]))

(defn diff [hash1 hash2]
  "Lists the changed files between two hashes in git:
    - if none of 'hash1' and 'hash2' is set: diff against local changes
    - if only one of 'hash1' or 'hash2' is set: diff between the hash and HEAD
    - if both 'hash1' and 'hash2' is set: diff changes between hash1 and hash2"
  (let [ws-path "."
        hash? (or hash1 hash2)
        hash-1 (when hash? (or hash1 "HEAD"))
        hash-2 (when hash? (or hash2 "HEAD"))
        files (if hash?
                (shell/sh "git" "diff" hash-1 hash-2 "--name-only" :dir ws-path)
                (shell/sh "git" "diff" "--name-only" :dir ws-path))]
    (str/split files #"\n")))

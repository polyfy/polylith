(ns polylith.git.core
  (:require [clojure.string :as str]
            [polylith.shell.interface :as shell]))

(defn diff [ws-path hash1 hash2]
  "Lists changes beteen two hashes.
   If none of 'hash1' and 'hash2' is set: diff against local changes.
   If both 'hash1' and 'hash2' is set: diff changes between hash1 and hash2.
   If only one of 'hash1' or 'hash2' is set: diff between the hash and HEAD."
  (let [hash? (or hash1 hash2)
        hash-1 (when hash? (or hash1 "HEAD"))
        hash-2 (when hash? (or hash2 "HEAD"))
        files (if hash?
                (shell/sh "git" "diff" hash-1 hash-2 "--name-only" :dir ws-path)
                (shell/sh "git" "diff" "--name-only" :dir ws-path))]
    (str/split files #"\n")))

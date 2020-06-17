(ns polylith.git.interface
  (:require [polylith.git.core :as core]))

(defn diff [sha1 sha2]
  "Lists the changed files that has occurred between two SHAs in git."
  (core/diff sha1 sha2))

(defn diff-command [sha1 sha2]
  "Returns the git diff command used to perform the diff."
  (core/diff-command sha1 sha2))

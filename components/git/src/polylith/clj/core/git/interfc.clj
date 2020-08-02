(ns polylith.clj.core.git.interfc
  (:require [polylith.clj.core.git.core :as core]))

(defn init [ws-path]
  (core/init ws-path))

(defn add [ws-path filename]
  (core/add ws-path filename))

(defn current-sha [ws-path]
  (core/current-sha ws-path))

(defn diff [sha1 sha2]
  "Lists the changed files that has occurred between two SHAs in git."
  (core/diff sha1 sha2))

(defn diff-command [sha1 sha2]
  "Returns the git diff command used to perform the diff."
  (core/diff-command sha1 sha2))

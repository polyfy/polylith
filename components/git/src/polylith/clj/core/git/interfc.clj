(ns polylith.clj.core.git.interfc
  (:require [polylith.clj.core.git.core :as core]))

(defn init [ws-dir]
  (core/init ws-dir))

(defn add [ws-dir filename]
  (core/add ws-dir filename))

(defn current-sha [ws-dir]
  (core/current-sha ws-dir))

(defn diff [ws-dir sha1 sha2]
  "Lists the changed files that has occurred between two SHAs in git."
  (core/diff ws-dir sha1 sha2))

(defn diff-command [sha1 sha2]
  "Returns the git diff command used to perform the diff."
  (core/diff-command sha1 sha2))

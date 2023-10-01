(ns ^:no-doc polylith.clj.core.git.interface
  (:require [polylith.clj.core.git.core :as core]
            [polylith.clj.core.git.tag :as tag]))

(def repo core/repo)
(def branch core/branch)

(defn is-git-repo? [ws-dir]
  (core/is-git-repo? ws-dir))

(defn is-polylith-repo? [ws-dir]
  (core/is-polylith-repo? ws-dir))

(defn init [ws-dir git-repo? branch]
  (core/init ws-dir git-repo? branch))

(defn add
  ([ws-dir filename]
   (core/add ws-dir filename true))
  ([ws-dir filename is-git-add]
   (core/add ws-dir filename is-git-add)))

(defn current-branch []
  (core/current-branch))

(defn root-dir []
  (core/root-dir))

(defn latest-polylith-sha [branch]
  (core/latest-polylith-sha branch))

(defn sha [ws-dir since tag-patterns]
  (core/sha ws-dir since tag-patterns))

(defn tags [ws-dir]
  (tag/tags ws-dir))

(defn diff
  "Lists the changed files that has occurred between two SHAs in git.
   If the workspace lives inside the git root in a separate directory,
   also remove the inner workspace directory from the diff."
  [ws-dir ws-local-dir is-no-changes changed-files sha1 sha2]
  (core/diff ws-dir ws-local-dir is-no-changes changed-files sha1 sha2))

(defn diff-command
  "Returns the git diff command used to perform the diff."
  [sha1 sha2]
  (core/diff-command sha1 sha2))

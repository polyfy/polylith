(ns polylith.clj.core.change.core
  (:require [polylith.clj.core.change.brick :as brick]
            [polylith.clj.core.change.environment :as env]
            [polylith.clj.core.git.interfc :as git]
            [polylith.clj.core.util.interfc :as util]))

(defn changed-files [environments sha1 sha2]
  "Returns changed components, bases and environments
    - if none of 'sha1' or 'sha2' is set: diff against local changes
    - if only one of 'sha1' or 'sha2' is set: diff between the SHA and HEAD
    - if both 'sha1' and 'sha2' is set: diff changes between sha1 and sha2"
   (let [sha? (or sha1 sha2)
         sha-1 (when sha? (or sha1 "HEAD"))
         sha-2 (when sha? (or sha2 "HEAD"))
         changed-files (git/diff sha1 sha2)
         {:keys [components bases]} (brick/bricks changed-files)
         changed-environments (env/changes environments components bases)]
     (util/ordered-map :sha1 sha-1
                       :sha2 sha-2
                       :git-command (git/diff-command sha-1 sha-2)
                       :changed-components components
                       :changed-bases bases
                       :changed-environments changed-environments
                       :changed-files changed-files)))

(defn with-changes [workspace changed-files]
  (assoc workspace :changes changed-files))

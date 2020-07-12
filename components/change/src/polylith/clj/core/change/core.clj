(ns polylith.clj.core.change.core
  (:require [polylith.clj.core.change.brick :as brick]
            [polylith.clj.core.change.indirect :as indirect]
            [polylith.clj.core.git.interfc :as git]
            [polylith.clj.core.util.interfc :as util]))

(defn changed-files [sha1 sha2]
  "Returns changed files.
    - if none of 'sha1' or 'sha2' is set: diff against local changes
    - if only one of 'sha1' or 'sha2' is set: diff between the SHA and HEAD
    - if both 'sha1' and 'sha2' is set: diff changes between sha1 and sha2"
  (let [sha? (or sha1 sha2)
        sha-1 (when sha? (or sha1 "HEAD"))
        sha-2 (when sha? (or sha2 "HEAD"))]
    {:sha1 sha-1
     :sha2 sha-2
     :files (git/diff sha-1 sha-2)}))

(defn changes [{:keys [environments]}
               {:keys [sha1 sha2 files]}]
   (let [deps (map (juxt :name :deps) environments)
         {:keys [components bases]} (brick/changed-bricks files)
         changed-bricks (set (concat components bases))
         indirect-changes (indirect/indirect-changes deps changed-bricks)]
     (util/ordered-map :sha1 sha1
                       :sha2 sha2
                       :git-command (git/diff-command sha1 sha2)
                       :changed-components components
                       :changed-bases bases
                       :indirect-changes indirect-changes
                       :changed-files files)))

(defn with-changes
  ([workspace]
   (with-changes workspace (changed-files nil nil)))
  ([workspace changed-files]
   (assoc workspace :changes (changes workspace changed-files))))

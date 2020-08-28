(ns polylith.clj.core.change.core
  (:require [polylith.clj.core.change.entity :as entity]
            [polylith.clj.core.change.indirect :as indirect]
            [polylith.clj.core.change.to-test :as to-test]
            [polylith.clj.core.util.interfc :as util]
            [polylith.clj.core.git.interfc :as git]
            [polylith.clj.core.util.interfc :as util]
            [polylith.clj.core.util.interfc.color :as color]))

(defn files [ws-dir sha1 sha2 color-mode]
  (try
    (git/diff ws-dir sha1 sha2)
    (catch Exception _
      (println (str (color/error color-mode "  Error: ") "Not a valid git repository"))
      (println)
      [])))

(defn changed-files-info [ws-dir sha1 sha2 color-mode]
    (util/ordered-map :sha1 sha1
                      :sha2 sha2
                      :files (files ws-dir sha1 sha2 color-mode)))

(defn changes [{:keys [ws-dir environments user-input]}
               {:keys [sha1 sha2 files]}]
   (let [deps (map (juxt :name :deps) environments)
         {:keys [run-all? run-env-tests?]} user-input
         {:keys [changed-components
                 changed-bases
                 changed-environments]} (entity/changed-entities ws-dir files)
         changed-bricks (set (concat changed-components changed-bases))
         env->indirect-changes (indirect/env->indirect-changes deps changed-bricks)
         env->bricks-to-test (to-test/env->bricks-to-test environments changed-components changed-bases env->indirect-changes run-all?)
         environments-to-test (to-test/environments-to-test ws-dir environments changed-bricks changed-environments run-env-tests?)]
     (util/ordered-map :sha1 sha1
                       :sha2 sha2
                       :git-command (git/diff-command sha1 sha2)
                       :changed-components changed-components
                       :changed-bases changed-bases
                       :changed-environments changed-environments
                       :env->indirect-changes env->indirect-changes
                       :env->bricks-to-test env->bricks-to-test
                       :environments-to-test environments-to-test
                       :changed-files files)))

(defn with-changes
  ([{:keys [ws-dir settings] :as workspace}]
   (let [{:keys [color-mode stable-since-tag-pattern]} settings
         sha (git/latest-stable-sha ws-dir stable-since-tag-pattern)]
     (with-changes workspace (changed-files-info ws-dir sha nil color-mode))))
  ([workspace changes-info]
   (assoc workspace :changes (changes workspace changes-info))))

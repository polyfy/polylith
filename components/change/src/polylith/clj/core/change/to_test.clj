(ns polylith.clj.core.change.to-test
  (:require [clojure.set :as set]
            [polylith.clj.core.change.environment :as env]
            [polylith.clj.core.path-finder.interfc.match :as m]
            [polylith.clj.core.path-finder.interfc.extract :as extract]
            [polylith.clj.core.path-finder.interfc.select :as select]))

(defn bricks-to-test-for-env [{:keys [name active? test-base-names test-component-names]}
                              changed-components
                              changed-bases
                              env->indirect-changes
                              run-all?]
  (let [brick-names (set (concat test-base-names test-component-names))
        changed-bricks (if active?
                         (if run-all?
                           brick-names
                           (set/intersection brick-names
                                             (set (concat changed-components changed-bases (env->indirect-changes name)))))
                         #{})]
    [name (vec (sort changed-bricks))]))

(defn env->bricks-to-test [environments changed-components changed-bases env->indirect-changes run-all?]
  (into {} (map #(bricks-to-test-for-env % changed-components changed-bases env->indirect-changes run-all?)
                environments)))

(defn has-test-dir? [ws-dir {:keys [src-paths test-paths profile-src-paths profile-test-paths]}]
  (let [path-entries (extract/path-entries ws-dir [src-paths test-paths profile-src-paths profile-test-paths])]
     (select/exists? path-entries m/environment? m/test-path? m/exists?)))

(defn environments-to-test [ws-dir environments changed-bricks changed-environments run-env-tests?]
  (let [indirectly-changed (env/indirectly-changed-environment-names environments changed-bricks)
        changed-envs (if run-env-tests?
                       (set (concat changed-environments indirectly-changed))
                       #{})
        environments-with-test-dir (set (map :name (filter #(has-test-dir? ws-dir %) environments)))]
    (vec (sort (set/intersection environments-with-test-dir changed-envs)))))

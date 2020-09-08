(ns polylith.clj.core.change.environments-to-test
  (:require [clojure.set :as set]
            [polylith.clj.core.path-finder.interface.criterias :as c]
            [polylith.clj.core.path-finder.interface.select :as select]
            [polylith.clj.core.path-finder.interface.extract :as extract]))

(defn included-environments [{:keys [src-paths test-paths profile-src-paths profile-test-paths]} disk-paths]
  (let [path-entries (extract/path-entries [src-paths test-paths profile-src-paths profile-test-paths] disk-paths)]
    (select/names path-entries c/environment? c/test-path? c/exists?)))

(defn environments-to-test [{:keys [name run-tests?] :as environment} disk-paths changed-environments run-env-tests?]
  (if (and run-tests? run-env-tests?)
    (let [included-envs (included-environments environment disk-paths)]
      [name (vec (sort (set/intersection (set changed-environments)
                                         (set included-envs))))])
    [name []]))

(defn env->environments-to-test [environments changed-environments disk-paths run-env-tests?]
  (into {} (map #(environments-to-test % disk-paths changed-environments run-env-tests?)
                environments)))

(ns polylith.clj.core.change.environments-to-test
  (:require [clojure.set :as set]
            [polylith.clj.core.path-finder.interface.criterias :as c]
            [polylith.clj.core.path-finder.interface.select :as select]
            [polylith.clj.core.path-finder.interface.extract :as extract]))

(defn included-environments [{:keys [src-paths test-paths profile-src-paths profile-test-paths]} disk-paths]
  (let [path-entries (extract/path-entries [src-paths test-paths profile-src-paths profile-test-paths] disk-paths)]
    (select/names path-entries c/environment? c/test-path? c/exists?)))

(defn select-envs [env environments dev?]
  (if dev?
    environments
    (if (= "development" env)
      []
      (set/difference (set environments) #{"development"}))))

(defn env-tests [env changed-environments included-envs dev?]
  (let [environments (set/intersection (set changed-environments)
                                       (set included-envs))]
    (select-envs env environments dev?)))

(defn environments-to-test [{:keys [name run-tests?] :as environment} disk-paths changed-environments dev? run-env-tests? run-all-tests?]
  (let [included-envs (included-environments environment disk-paths)]
    (cond
      run-all-tests? [name (vec (sort (select-envs name included-envs dev?)))]
      (and run-tests? run-env-tests?) [name (vec (sort (env-tests name changed-environments included-envs dev?)))]
      :else [name []])))

(defn env->environments-to-test [environments changed-environments disk-paths dev? run-env-tests? run-all-tests?]
  (into {} (map #(environments-to-test % disk-paths changed-environments dev? run-env-tests? run-all-tests?)
                environments)))

(ns polylith.clj.core.change.environments-to-test
  (:require [clojure.set :as set]
            [polylith.clj.core.path-finder.interface.criterias :as c]
            [polylith.clj.core.path-finder.interface.select :as select]
            [polylith.clj.core.path-finder.interface.extract :as extract]))

(defn included-environments [ws-dir {:keys [src-paths test-paths profile-src-paths profile-test-paths]}]
  (let [path-entries (extract/path-entries ws-dir [src-paths test-paths profile-src-paths profile-test-paths])]
    (select/names path-entries c/environment? c/test-path? c/exists?)))

(defn environments-to-test [ws-dir {:keys [name run-tests?] :as environment} changed-environments run-env-tests?]
  (if (and run-tests? run-env-tests?)
    (let [included-envs (included-environments ws-dir environment)]
      [name (vec (sort (set/intersection (set changed-environments)
                                         (set included-envs))))])
    [name []]))

(defn env->environments-to-test [ws-dir environments changed-environments run-env-tests?]
  (into {} (map #(environments-to-test ws-dir % changed-environments run-env-tests?)
                environments)))

;(require '[dev.jocke :as z])
;(def workspace z/workspace)
;
;(def changes (:changes workspace))
;(def environments (:environments workspace))
;(def environment (common/find-environment "poly" environments))
;
;(def changed-environments (:changed-environments changes))
;(def included-envs (set (included-environments "." environment)))
;
;
;
;(map #(environments-to-test "." % changed-environments true)
;     environments)
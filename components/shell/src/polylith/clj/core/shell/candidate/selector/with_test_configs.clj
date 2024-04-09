(ns ^:no-doc polylith.clj.core.shell.candidate.selector.with-test-configs
  (:require [clojure.set :as set]
            [polylith.clj.core.shell.candidate.creators :as c]
            [polylith.clj.core.shell.candidate.shared :as shared])
  (:refer-clojure :exclude [alias]))

(defn config-names [candidate _ {:keys [configs]}]
  (let [group-id (-> candidate :group :id)
        test-configs (-> configs :workspace :test-configs keys)]
    (map #(c/single-txt (name %) (c/group group-id) true)
         test-configs)))

(defn select [{:keys [group]} groups {:keys [settings configs]}]
  (let [color-mode (:color-mode settings)
        test-configs (set (map name (-> configs :workspace :test-configs keys)))]
    (mapv #(c/fn-explorer-child % :test color-mode true group #'select)
          (sort (set/difference
                  test-configs
                  (set (shared/args groups group)))))))

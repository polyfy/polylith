(ns polylith.clj.core.shell.candidate.selector.color-modes
  (:require [clojure.set :as set]
            [polylith.clj.core.shell.candidate.creators :as c]
            [polylith.clj.core.shell.candidate.shared :as shared]))

(defn select [{:keys [group]} groups {:keys [settings]}]
  (let [color-mode (:color-mode settings)]
    (mapv #(c/fn-explorer-child % :project color-mode false group #'select)
          (sort (set/difference
                  #{"none" "dark" "light"}
                  (set (shared/args groups group)))))))

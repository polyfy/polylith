(ns ^:no-doc polylith.clj.core.shell.candidate.selector.outdated-libs
  (:require [clojure.set :as set]
            [polylith.clj.core.shell.candidate.creators :as c]
            [polylith.clj.core.lib.interface :as lib]
            [polylith.clj.core.shell.candidate.shared :as shared]))

(defn select [{:keys [group]} groups workspace]
  (let [libraries (lib/outdated-libs workspace)
        _ (tap> {:libraries libraries})]
    (mapv #(c/fn-explorer-child-plain % true group #'select)
          (sort (set/difference
                  (set libraries)
                  (set (shared/args groups group)))))))

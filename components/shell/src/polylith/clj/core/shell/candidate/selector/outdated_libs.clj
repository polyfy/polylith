(ns ^:no-doc polylith.clj.core.shell.candidate.selector.outdated-libs
  (:require [clojure.set :as set]
            [polylith.clj.core.antq.ifc :as antq]
            [polylith.clj.core.shell.candidate.creators :as c]
            [polylith.clj.core.lib.interface :as lib]
            [polylith.clj.core.shell.candidate.shared :as shared]))

(defn select [{:keys [group]} groups {:keys [configs user-input]}]
  (let [library->latest-version (antq/library->latest-version configs user-input)
        libraries (lib/outdated-libs library->latest-version)]
    (mapv #(c/fn-explorer-child-plain % true group #'select)
          (sort (set/difference
                  (set libraries)
                  (set (shared/args groups group)))))))

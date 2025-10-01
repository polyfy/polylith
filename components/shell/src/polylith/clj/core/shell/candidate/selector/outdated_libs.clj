(ns ^:no-doc polylith.clj.core.shell.candidate.selector.outdated-libs
  (:require [clojure.set :as set]
            [polylith.clj.core.antq.ifc :as antq]
            [polylith.clj.core.shell.candidate.creators :as c]
            [polylith.clj.core.lib.interface :as lib]
            [polylith.clj.core.shell.candidate.shared :as shared]))

(def outdated-libs (atom nil))

(defn reset-outdated-libraries []
  (reset! outdated-libs nil))

(defn set-outdated-libs [configs]
  (let [library->latest-version (antq/library->latest-version configs true)
        libraries (lib/outdated-libs library->latest-version)]
    (reset! outdated-libs libraries)))

(defn select [{:keys [group]} groups {:keys [configs]}]
  (when (-> outdated-libs deref nil?)
    (set-outdated-libs configs))
  (mapv #(c/fn-explorer-child-plain % true group #'select)
      (sort (set/difference
              (set @outdated-libs)
              (set (shared/args groups group))))))

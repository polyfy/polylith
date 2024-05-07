(ns ^:no-doc polylith.clj.core.info.table.ws-column.external.cell
  (:require [polylith.clj.core.path-finder.interface.extract :as extract]
            [polylith.clj.core.text-table.interface :as text-table]))

(defn full-brick-paths [type name name->paths]
  (let [{:keys [src test]} (name->paths name)]
    {:src (mapv #(str (clojure.core/name type) "s/" name "/" %) src)
     :test (mapv #(str (clojure.core/name type) "s/" name "/" %) test)}))

(defn ->path-entries [{:keys [alias type name]} alias->workspace]
  (let [{:keys [components bases paths]} (alias->workspace alias)
        name->paths (into {} (map (juxt :name :paths)
                                  (concat components bases)))
        brick-paths (full-brick-paths type name name->paths)]
    (extract/from-paths brick-paths paths)))

(defn cell [index start-row column {:keys [name] :as profile} bricks-to-test alias->workspace is-show-resources status-flags-fn]
  (let [path-entries (->path-entries profile alias->workspace)
        statuses (status-flags-fn name bricks-to-test path-entries is-show-resources)]
    (text-table/cell column (+ index start-row 3) statuses :purple :center)))

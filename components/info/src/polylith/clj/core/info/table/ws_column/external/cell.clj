(ns ^:no-doc polylith.clj.core.info.table.ws-column.external.cell
  (:require [polylith.clj.core.path-finder.interface.extract :as extract]
            [polylith.clj.core.text-table.interface :as text-table]))

(defn brick-names [base-names component-names source]
  (set (concat (source base-names)
               (source component-names))))

(defn brick-paths [alias name type paths brick-names-in-project]
  (if (contains? brick-names-in-project
                 (str alias "/" name))
    (mapv #(str (clojure.core/name type) "s/" name "/" %)
          paths)
    []))

(defn full-brick-paths [type alias name base-names component-names name->paths]
  (let [{:keys [src test]} (name->paths name)
        brick-names-src (brick-names base-names component-names :src)
        brick-names-test (brick-names base-names component-names :test)]
    {:src (brick-paths alias name type src brick-names-src)
     :test (brick-paths alias name type test brick-names-test)}))

(defn ->path-entries [{:keys [alias type name]} base-names component-names alias->workspace]
  (let [{:keys [components bases paths]} (alias->workspace alias)
        name->paths (into {} (map (juxt :name :paths)
                                  (concat components bases)))
        brick-paths (full-brick-paths type alias name base-names component-names name->paths)]
    (extract/from-paths brick-paths paths)))

(defn cell [index start-row column {:keys [name] :as ws-brick} base-names component-names bricks-to-test alias->workspace is-show-resources status-flags-fn]
  (let [path-entries (->path-entries ws-brick base-names component-names alias->workspace)
        statuses (status-flags-fn name bricks-to-test path-entries is-show-resources)]
    (text-table/cell column (+ index start-row 3) statuses :purple :center)))

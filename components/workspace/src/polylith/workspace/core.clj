(ns polylith.workspace.core
  (:require [clojure.tools.deps.alpha.util.maven :as mvn]
            [polylith.common.interface :as common]
            [polylith.deps.interface :as deps]
            [polylith.validate.interface :as validate]
            [polylith.workspace.calculate-interfaces :as ifcs]
            [polylith.workspace.lib-imports :as lib-imports]
            [polylith.util.interface :as util]
            [polylith.workspace-clj.interface :as ws-clojure]))


(defn loc
  ([entities]
   (apply + (map :loc entities)))
 ([brick-names brick->loc]
  (apply + (map brick->loc brick-names))))

(defn enrich-component [top-ns interface-names {:keys [name type namespaces interface] :as component}]
  (let [interface-deps (deps/brick-interface-deps top-ns interface-names component)
        lib-imports (lib-imports/lib-imports top-ns interface-names component)]
    (array-map :name name
               :type type
               :loc (loc namespaces)
               :interface interface
               :namespaces namespaces
               :lib-imports lib-imports
               :interface-deps interface-deps)))

(defn enrich-base [top-ns interface-names {:keys [name type namespaces] :as base}]
  (let [interface-deps (deps/brick-interface-deps top-ns interface-names base)
        lib-imports (lib-imports/lib-imports top-ns interface-names base)]
    (array-map :name name
               :type type
               :loc (loc namespaces)
               :namespaces namespaces
               :lib-imports lib-imports
               :interface-deps interface-deps)))

(defn enrich-env [{:keys [name group test? type components bases paths deps]}
                  brick->lib-imports
                  brick->loc]
  (let [brick-names (concat components bases)
        lib-imports (mapcat brick->lib-imports brick-names)
        lines-of-code (loc brick-names brick->loc)]
    (util/ordered-map :name name
                      :group group
                      :test? test?
                      :type type
                      :loc lines-of-code
                      :components components
                      :bases bases
                      :paths paths
                      :lib-imports (vec (sort (set lib-imports)))
                      :deps deps)))

(defn brick->lib-imports [brick]
  (into {} (mapv (juxt :name :lib-imports) brick)))

(defn enrich-settings [{:keys [maven-repos] :as settings}]
  (assoc settings :maven-repos (merge mvn/standard-repos maven-repos)))

(defn enrich-workspace [{:keys [ws-path settings components bases environments]}]
  (let [top-ns (common/top-namespace (:top-namespace settings))
        interfaces (ifcs/interfaces components)
        interface-names (apply sorted-set (mapv :name interfaces))
        enriched-components (mapv #(enrich-component top-ns interface-names %) components)
        enriched-bases (mapv #(enrich-base top-ns interface-names %) bases)
        enriched-bricks (concat enriched-components enriched-bases)
        enriched-interfaces (deps/interface-deps interfaces enriched-components)
        brick->lib-imports (brick->lib-imports enriched-bricks)
        brick->loc (into {} (map (juxt :name :loc) enriched-bricks))
        enriched-environments (mapv #(enrich-env % brick->lib-imports brick->loc) environments)
        enriched-settings (enrich-settings settings)
        warnings (validate/warnings interfaces components)
        errors (validate/errors top-ns interface-names enriched-interfaces enriched-components bases)]
    (array-map :ws-path ws-path
               :settings enriched-settings
               :interfaces enriched-interfaces
               :components enriched-components
               :bases enriched-bases
               :environments enriched-environments
               :messages {:warnings warnings
                          :errors   errors})))

(def workspace (-> "../clojure-polylith-realworld-example-app"
                   ws-clojure/workspace-from-disk
                   enrich-workspace))

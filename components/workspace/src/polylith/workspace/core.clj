(ns polylith.workspace.core
  (:require [clojure.tools.deps.alpha.util.maven :as mvn]
            [polylith.common.interface :as common]
            [polylith.deps.interface :as deps]
            [polylith.validate.interface :as validate]
            [polylith.workspace.calculate-interfaces :as ifcs]
            [polylith.workspace.lib-imports :as lib-imports]
            [polylith.util.interface :as util]))

(defn enrich-component [top-ns interface-names {:keys [name type namespaces interface] :as component}]
  (let [interface-deps (deps/brick-interface-deps top-ns interface-names component)
        lib-imports (lib-imports/lib-imports top-ns interface-names component)]
    (array-map :name name
               :type type
               :interface interface
               :namespaces namespaces
               :lib-imports lib-imports
               :interface-deps interface-deps)))

(defn enrich-base [top-ns interface-names {:keys [name type namespaces] :as base}]
  (let [interface-deps (deps/brick-interface-deps top-ns interface-names base)
        lib-imports (lib-imports/lib-imports top-ns interface-names base)]
    (array-map :name name
               :type type
               :namespaces namespaces
               :lib-imports lib-imports
               :interface-deps interface-deps)))

(defn enrich-env [{:keys [name group test? type components bases paths deps]}
                  component->lib-imports base->lib-imports]
  (let [lib-imports (concat (mapcat component->lib-imports components)
                            (mapcat base->lib-imports components))]
    (util/ordered-map :name name
                      :group group
                      :test? test?
                      :type type
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
        enriched-interfaces (deps/interface-deps interfaces enriched-components)
        component->lib-imports (brick->lib-imports enriched-components)
        base->lib-imports (brick->lib-imports enriched-bases)
        enriched-environments (mapv #(enrich-env % component->lib-imports base->lib-imports) environments)
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

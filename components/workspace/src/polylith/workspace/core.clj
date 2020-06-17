(ns polylith.workspace.core
  (:require [clojure.tools.deps.alpha.util.maven :as mvn]
            [polylith.common.interface :as common]
            [polylith.deps.interface :as deps]
            [polylith.validate.interface :as validate]
            [polylith.workspace.calculate-interfaces :as ifcs]
            [polylith.workspace.lib-imports :as lib-imports]
            [polylith.util.interface :as util]))

(defn pimp-component [top-ns interface-names {:keys [name type imports interface] :as component}]
  (let [interface-deps (deps/brick-interface-deps top-ns interface-names component)
        lib-imports (lib-imports/lib-imports top-ns interface-names component)]
    (array-map :name name
               :type type
               :interface interface
               :imports imports
               :lib-imports lib-imports
               :interface-deps interface-deps)))

(defn pimp-base [top-ns interface-names {:keys [name type imports] :as base}]
  (let [interface-deps (deps/brick-interface-deps top-ns interface-names base)
        lib-imports (lib-imports/lib-imports top-ns interface-names base)]
    (array-map :name name
               :type type
               :imports imports
               :lib-imports lib-imports
               :interface-deps interface-deps)))

(defn pimp-env [{:keys [name group test? type components bases paths deps]}
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

(defn pimp-settings [{:keys [maven-repos] :as settings}]
  (assoc settings :maven-repos (merge mvn/standard-repos maven-repos)))

(defn pimp-workspace [{:keys [ws-path settings components bases environments]}]
  (let [top-ns (common/top-namespace (:top-namespace settings))
        interfaces (ifcs/interfaces components)
        interface-names (apply sorted-set (mapv :name interfaces))
        pimped-components (mapv #(pimp-component top-ns interface-names %) components)
        pimped-bases (mapv #(pimp-base top-ns interface-names %) bases)
        pimped-interfaces (deps/interface-deps interfaces pimped-components)
        component->lib-imports (brick->lib-imports pimped-components)
        base->lib-imports (brick->lib-imports pimped-bases)
        pimped-environments (mapv #(pimp-env % component->lib-imports base->lib-imports) environments)
        pimped-settings (pimp-settings settings)
        warnings (validate/warnings interfaces components)
        errors (validate/errors top-ns interface-names pimped-interfaces pimped-components bases)]
    (array-map :ws-path ws-path
               :settings pimped-settings
               :interfaces pimped-interfaces
               :components pimped-components
               :bases pimped-bases
               :environments pimped-environments
               :messages {:warnings warnings
                          :errors errors})))

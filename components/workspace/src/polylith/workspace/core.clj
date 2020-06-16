(ns polylith.workspace.core
  (:require [clojure.tools.deps.alpha.util.maven :as mvn]
            [polylith.deps.interface :as deps]
            [polylith.validate.interface :as validate]
            [polylith.workspace.calculate-interfaces :as ifcs]
            [polylith.workspace.lib-imports :as lib-imports]
            [polylith.shared.interface :as shared]))

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

(defn pimp-workspace [{:keys [ws-path mvn/repos settings components bases environments deps paths]}]
  (let [top-ns (shared/top-namespace (:top-namespace settings))
        interfaces (ifcs/interfaces components)
        interface-names (apply sorted-set (mapv :name interfaces))
        pimped-components (mapv #(pimp-component top-ns interface-names %) components)
        pimped-bases (mapv #(pimp-base top-ns interface-names %) bases)
        pimped-interfaces (deps/interface-deps interfaces pimped-components)
        warnings (validate/warnings interfaces components)
        errors (validate/errors top-ns interface-names pimped-interfaces pimped-components bases)]
    (array-map :ws-path ws-path
               :mvn/repos (merge mvn/standard-repos repos)
               :deps deps
               :paths paths
               :settings settings
               :interfaces pimped-interfaces
               :components pimped-components
               :bases pimped-bases
               :environments environments
               :messages {:warnings warnings
                          :errors errors})))

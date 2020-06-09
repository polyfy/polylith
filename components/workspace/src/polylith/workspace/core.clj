(ns polylith.workspace.core
  (:require [polylith.deps.interface :as deps]
            [polylith.validate.interface :as validate]
            [polylith.workspace.interfaces :as ifcs]
            [polylith.shared.interface :as shared]))

(defn pimp-workspace [{:keys [polylith components bases environments]}]
  (let [top-ns (shared/top-namespace (:top-namespace polylith))
        interfaces (ifcs/interfaces components)
        interface-names (apply sorted-set (mapv :name interfaces))
        pimped-components (mapv #(deps/with-deps top-ns interface-names %) components)
        pimped-bases (mapv #(deps/with-deps top-ns interface-names %) bases)
        pimped-interfaces (deps/interface-deps interfaces pimped-components)
        warnings (validate/warnings interfaces components)
        errors (validate/errors top-ns interface-names pimped-interfaces pimped-components bases)]
    {:polylith polylith
     :interfaces pimped-interfaces
     :components pimped-components
     :bases pimped-bases
     :environments environments
     :messages {:warnings warnings
                :errors errors}}))

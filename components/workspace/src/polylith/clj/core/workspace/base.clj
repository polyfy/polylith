(ns polylith.clj.core.workspace.base
  (:require [polylith.clj.core.deps.interface :as deps]
            [polylith.clj.core.workspace.loc :as loc]
            [polylith.clj.core.workspace.lib-imports :as lib-imp]))

(defn enrich [suffixed-top-ns interface-names {:keys [namespaces] :as base}]
  (let [interface-deps (deps/interface-deps suffixed-top-ns interface-names base)
        lib-imports (lib-imp/lib-imports suffixed-top-ns interface-names base)]
    (assoc base :lines-of-code (loc/lines-of-code namespaces)
                :namespaces namespaces
                :lib-imports lib-imports
                :interface-deps interface-deps)))

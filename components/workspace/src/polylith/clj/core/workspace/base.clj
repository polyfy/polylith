(ns ^:no-doc polylith.clj.core.workspace.base
  (:require [polylith.clj.core.deps.interface :as deps]
            [polylith.clj.core.workspace.loc :as loc]
            [polylith.clj.core.workspace.lib-imports :as lib-imp]))

(defn enrich [ws-dir suffixed-top-ns bases interface-names {:keys [namespaces] :as base}]
  (let [interface-deps (deps/interface-deps suffixed-top-ns interface-names base)
        base-deps (deps/base-deps bases base suffixed-top-ns)
        lib-imports (lib-imp/lib-imports suffixed-top-ns interface-names base)]
    (assoc base :lines-of-code (loc/lines-of-code ws-dir namespaces)
                :namespaces namespaces
                :lib-imports lib-imports
                :base-deps base-deps
                :interface-deps interface-deps)))

(ns polylith.clj.core.workspace.component
  (:require [polylith.clj.core.deps.interface :as deps]
            [polylith.clj.core.workspace.loc :as loc]
            [polylith.clj.core.workspace.lib-imports :as lib-imp]))

(defn enrich [suffixed-top-ns interface-names {:keys [namespaces] :as component}]
  (let [interface-deps (deps/interface-deps suffixed-top-ns interface-names component)
        lib-imports (lib-imp/lib-imports suffixed-top-ns interface-names component)
        lines-of-code (loc/lines-of-code namespaces)]
    (assoc component :lines-of-code lines-of-code
                     :lib-imports lib-imports
                     :interface-deps interface-deps)))

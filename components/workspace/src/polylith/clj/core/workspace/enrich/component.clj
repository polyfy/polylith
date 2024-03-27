(ns ^:no-doc polylith.clj.core.workspace.enrich.component
  (:require [polylith.clj.core.deps.interface :as deps]
            [polylith.clj.core.lib.interface :as lib]
            [polylith.clj.core.workspace.enrich.loc :as loc]
            [polylith.clj.core.workspace.enrich.lib-imports :as lib-imp]))

(defn enrich [ws-dir suffixed-top-ns interface-names outdated-libs library->latest-version user-input name-type->keep-lib-versions workspaces {:keys [name type namespaces lib-deps] :as component}]
  (let [interface-deps (deps/interface-deps suffixed-top-ns interface-names workspaces component)
        lib-imports (lib-imp/lib-imports suffixed-top-ns interface-names component)
        lines-of-code (loc/lines-of-code ws-dir namespaces)
        lib-deps (lib/lib-deps-with-latest-version name type lib-deps outdated-libs library->latest-version user-input name-type->keep-lib-versions)]
    (assoc component :lib-deps lib-deps
                     :lines-of-code lines-of-code
                     :lib-imports lib-imports
                     :interface-deps interface-deps)))

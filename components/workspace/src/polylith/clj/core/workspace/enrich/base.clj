(ns ^:no-doc polylith.clj.core.workspace.enrich.base
  (:require [polylith.clj.core.deps.interface :as deps]
            [polylith.clj.core.lib.interface :as lib]
            [polylith.clj.core.workspace.enrich.loc :as loc]
            [polylith.clj.core.workspace.enrich.lib-imports :as lib-imp]))

(defn enrich [ws-dir suffixed-top-ns interface-names base-names outdated-libs library->latest-version user-input name-type->keep-lib-version interface-ns
              {:keys [name type namespaces lib-deps] :as base}]
  (let [{:keys [interface-deps base-deps illegal-deps]} (deps/brick-deps suffixed-top-ns interface-names base-names interface-ns base)
        lib-imports (lib-imp/lib-imports suffixed-top-ns interface-names base)
        lib-deps (lib/lib-deps-with-latest-version name type lib-deps outdated-libs library->latest-version user-input name-type->keep-lib-version)]
    (cond-> (assoc base :lib-deps lib-deps
                        :lines-of-code (loc/lines-of-code ws-dir namespaces)
                        :namespaces namespaces
                        :lib-imports lib-imports
                        :interface-deps interface-deps
                        :base-deps base-deps)
            (seq illegal-deps) (assoc :illegal-deps illegal-deps))))

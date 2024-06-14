(ns ^:no-doc polylith.clj.core.workspace.enrich.component
  (:require [polylith.clj.core.deps.interface :as deps]
            [polylith.clj.core.lib.interface :as lib]
            [polylith.clj.core.workspace.enrich.loc :as loc]
            [polylith.clj.core.workspace.enrich.lib-imports :as lib-imp]))

(defn has-deps? [{:keys [src test]}]
  (or (seq src)
      (seq test)))

(defn enrich [ws-dir suffixed-top-ns interface-names base-names outdated-libs library->latest-version user-input name-type->keep-lib-versions interface-ns
              {:keys [name type namespaces lib-deps] :as component}]
  (let [{:keys [interface-deps _ illegal-deps]} (deps/brick-deps suffixed-top-ns interface-names base-names interface-ns component)
        lib-imports (lib-imp/lib-imports suffixed-top-ns interface-names component)
        lines-of-code (loc/lines-of-code ws-dir namespaces)
        lib-deps (lib/lib-deps-with-latest-version name type lib-deps outdated-libs library->latest-version user-input name-type->keep-lib-versions)]
    (cond-> (assoc component :lib-deps lib-deps
                             :lines-of-code lines-of-code
                             :lib-imports lib-imports
                             :interface-deps interface-deps)
            (seq illegal-deps) (assoc :illegal-deps illegal-deps))))

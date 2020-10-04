(ns polylith.clj.core.workspace.component
  (:require [polylith.clj.core.deps.interface :as deps]
            [polylith.clj.core.workspace.loc :as loc]
            [polylith.clj.core.lib.interface :as lib]
            [polylith.clj.core.workspace.lib-imports :as lib-imp]))

(defn enrich [suffixed-top-ns interface-names settings {:keys [namespaces-src namespaces-test] :as component}]
  (let [interface-deps (deps/interface-deps suffixed-top-ns interface-names component)
        lib-imports-src (lib-imp/lib-imports-src suffixed-top-ns interface-names component)
        lib-imports-test (lib-imp/lib-imports-test suffixed-top-ns interface-names component)
        lines-of-code-src (loc/lines-of-code namespaces-src)
        lines-of-code-test (loc/lines-of-code namespaces-test)
        {:keys [included-libs]} (lib/dependencies settings component)]
    (assoc component :lines-of-code-src lines-of-code-src
                     :lines-of-code-test lines-of-code-test
                     :namespaces-src namespaces-src
                     :namespaces-test namespaces-test
                     :lib-imports-src lib-imports-src
                     :lib-imports-test lib-imports-test
                     :interface-deps interface-deps
                     :lib-dep-names included-libs)))

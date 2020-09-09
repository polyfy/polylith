(ns polylith.clj.core.workspace.base
  (:require [polylith.clj.core.deps.interface :as deps]
            [polylith.clj.core.workspace.loc :as loc]
            [polylith.clj.core.lib-dep.interface :as lib-dep]
            [polylith.clj.core.workspace.lib-imports :as lib]))

(defn enrich [suffixed-top-ns interface-names settings {:keys [namespaces-src namespaces-test] :as base}]
  (let [interface-deps (deps/interface-deps suffixed-top-ns interface-names base)
        lib-imports-src (lib/lib-imports-src suffixed-top-ns interface-names base)
        lib-imports-test (lib/lib-imports-test suffixed-top-ns interface-names base)
        {:keys [included-libs]} (lib-dep/dependencies settings base)]
    (assoc base :lines-of-code-src (loc/lines-of-code namespaces-src)
                :lines-of-code-test (loc/lines-of-code namespaces-test)
                :namespaces-test namespaces-test
                :lib-imports-src lib-imports-src
                :lib-imports-test lib-imports-test
                :interface-deps interface-deps
                :lib-dep-names included-libs)))

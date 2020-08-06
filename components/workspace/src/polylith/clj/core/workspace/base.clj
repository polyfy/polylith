(ns polylith.clj.core.workspace.base
  (:require [polylith.clj.core.deps.interfc :as deps]
            [polylith.clj.core.workspace.loc :as loc]
            [polylith.clj.core.workspace.lib-deps :as libd]
            [polylith.clj.core.workspace.lib-imports :as lib]))

(defn enrich [suffixed-top-ns interface-names settings {:keys [name type namespaces-src namespaces-test] :as base}]
  (let [interface-deps (deps/interface-deps suffixed-top-ns interface-names base)
        lib-imports-src (lib/lib-imports-src suffixed-top-ns interface-names base)
        lib-imports-test (lib/lib-imports-test suffixed-top-ns interface-names base)
        lib-deps (libd/deps settings base)]
    (array-map :name name
               :type type
               :lines-of-code-src (loc/lines-of-code namespaces-src)
               :lines-of-code-test (loc/lines-of-code namespaces-test)
               :namespaces-src namespaces-src
               :namespaces-test namespaces-test
               :lib-imports-src lib-imports-src
               :lib-imports-test lib-imports-test
               :interface-deps interface-deps
               :lib-deps lib-deps)))

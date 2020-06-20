(ns polylith.workspace.core
  (:require [clojure.tools.deps.alpha.util.maven :as mvn]
            [polylith.common.interface :as common]
            [polylith.deps.interface :as deps]
            [polylith.validate.interface :as validate]
            [polylith.workspace.calculate-interfaces :as ifcs]
            [polylith.workspace.lib-imports :as lib-imports]
            [polylith.util.interface :as util]
            [polylith.file.interface :as file]
            [polylith.workspace-clj.interface :as ws-clojure]))

(defn brick-loc [namespaces]
  (apply + (mapv #(file/lines-of-code %)
                 (mapv :file-path namespaces))))

(defn env-loc [brick-names brick->loc]
  (let [locs (map brick->loc brick-names)]
    {:loc-src (apply + (map :lines-of-code-src locs))
     :loc-test (apply + (map :lines-of-code-test locs))}))

(defn enrich-component [top-ns interface-names {:keys [name type namespaces-src namespaces-test interface] :as component}]
  (let [interface-deps (deps/brick-interface-deps top-ns interface-names component)
        lib-imports-src (lib-imports/lib-imports-src top-ns interface-names component)
        lib-imports-test (lib-imports/lib-imports-test top-ns interface-names component)]
    (array-map :name name
               :type type
               :lines-of-code-src (brick-loc namespaces-src)
               :lines-of-code-test (brick-loc namespaces-test)
               :interface interface
               :namespaces-src namespaces-src
               :namespaces-test namespaces-test
               :lib-imports-src lib-imports-src
               :lib-imports-test lib-imports-test
               :interface-deps interface-deps)))

(defn enrich-base [top-ns interface-names {:keys [name type namespaces-src namespaces-test] :as base}]
  (let [interface-deps (deps/brick-interface-deps top-ns interface-names base)
        lib-imports-src (lib-imports/lib-imports-src top-ns interface-names base)
        lib-imports-test (lib-imports/lib-imports-test top-ns interface-names base)]
    (array-map :name name
               :type type
               :lines-of-code-src (brick-loc namespaces-src)
               :lines-of-code-test (brick-loc namespaces-test)
               :namespaces-src namespaces-src
               :namespaces-test namespaces-test
               :lib-imports-src lib-imports-src
               :lib-imports-test lib-imports-test
               :interface-deps interface-deps)))

(defn enrich-env [{:keys [name group test? type component-names base-names paths deps]}
                  brick->lib-imports-src
                  brick->lib-imports-test
                  brick->loc]
  (let [brick-names (concat component-names base-names)
        brick->lib-imports (if test? brick->lib-imports-test brick->lib-imports-src)
        lib-imports (-> (mapcat brick->lib-imports brick-names) set sort vec)
        {:keys [loc-src loc-test]} (env-loc brick-names brick->loc)
        lines-of-code (if test? loc-test loc-src)]
    (util/ordered-map :name name
                      :group group
                      :test? test?
                      :type type
                      :lines-of-code lines-of-code
                      :component-names component-names
                      :base-names base-names
                      :paths paths
                      :lib-imports lib-imports
                      :deps deps)))

(defn brick->lib-imports-src [brick]
  (into {} (mapv (juxt :name :lib-imports-src) brick)))

(defn brick->lib-imports-test [brick]
  (into {} (mapv (juxt :name :lib-imports-test) brick)))

(defn enrich-settings [{:keys [maven-repos] :as settings}]
  (assoc settings :maven-repos (merge mvn/standard-repos maven-repos)))

(defn enrich-workspace [{:keys [ws-path settings components bases environments]}]
  (let [top-ns (common/top-namespace (:top-namespace settings))
        interfaces (ifcs/interfaces components)
        interface-names (apply sorted-set (mapv :name interfaces))
        enriched-components (mapv #(enrich-component top-ns interface-names %) components)
        enriched-bases (mapv #(enrich-base top-ns interface-names %) bases)
        enriched-bricks (concat enriched-components enriched-bases)
        enriched-interfaces (deps/interface-deps interfaces enriched-components)
        brick->lib-imports-src (brick->lib-imports-src enriched-bricks)
        brick->lib-imports-test (brick->lib-imports-test enriched-bricks)
        brick->loc (into {} (map (juxt :name #(select-keys % [:lines-of-code-src :lines-of-code-test])) enriched-bricks))
        enriched-environments (mapv #(enrich-env % brick->lib-imports-src brick->lib-imports-test brick->loc) environments)
        enriched-settings (enrich-settings settings)
        warnings (validate/warnings interfaces components)
        errors (validate/errors top-ns interface-names enriched-interfaces enriched-components enriched-bases enriched-environments)]
    (array-map :ws-path ws-path
               :settings enriched-settings
               :interfaces enriched-interfaces
               :components enriched-components
               :bases enriched-bases
               :environments enriched-environments
               :messages {:warnings warnings
                          :errors   errors})))

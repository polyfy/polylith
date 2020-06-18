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

(defn enrich-component [top-ns interface-names {:keys [name type namespaces-src test-namespaces interface] :as component}]
  (let [interface-deps (deps/brick-interface-deps top-ns interface-names component)
        lib-imports-src (lib-imports/lib-imports-src top-ns interface-names component)
        lib-imports-test (lib-imports/lib-imports-test top-ns interface-names component)]
    (array-map :name name
               :type type
               :lines-of-code-src (brick-loc namespaces-src)
               :lines-of-code-test (brick-loc test-namespaces)
               :interface interface
               :namespaces-src namespaces-src
               :test-namespaces test-namespaces
               :lib-imports-src lib-imports-src
               :lib-imports-test lib-imports-test
               :interface-deps interface-deps)))

(defn enrich-base [top-ns interface-names {:keys [name type namespaces-src test-namespaces] :as base}]
  (let [interface-deps (deps/brick-interface-deps top-ns interface-names base)
        lib-imports-src (lib-imports/lib-imports-src top-ns interface-names base)
        lib-imports-test (lib-imports/lib-imports-test top-ns interface-names base)]
    (array-map :name name
               :type type
               :lines-of-code-src (brick-loc namespaces-src)
               :lines-of-code-test (brick-loc test-namespaces)
               :namespaces-src namespaces-src
               :test-namespaces test-namespaces
               :lib-imports-src lib-imports-src
               :lib-imports-test lib-imports-test
               :interface-deps interface-deps)))

(defn enrich-env [{:keys [name group test? type components bases paths deps]}
                  brick->lib-imports-src
                  brick->lib-imports-test
                  brick->loc]
  (let [brick-names (concat components bases)
        lib-imports-src (mapcat brick->lib-imports-src brick-names)
        lib-imports-test (mapcat brick->lib-imports-test brick-names)
        {:keys [loc-src loc-test]} (env-loc brick-names brick->loc)]
    (util/ordered-map :name name
                      :group group
                      :test? test?
                      :type type
                      :lines-of-code-src loc-src
                      :lines-of-code-test loc-test
                      :components components
                      :bases bases
                      :paths paths
                      :lib-imports-src (vec (sort (set lib-imports-src)))
                      :lib-imports-test (vec (sort (set lib-imports-test)))
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
        errors (validate/errors top-ns interface-names enriched-interfaces enriched-components bases)]
    (array-map :ws-path ws-path
               :settings enriched-settings
               :interfaces enriched-interfaces
               :components enriched-components
               :bases enriched-bases
               :environments enriched-environments
               :messages {:warnings warnings
                          :errors   errors})))

(-> "../clojure-polylith-realworld-example-app"
    ws-clojure/workspace-from-disk
    enrich-workspace)

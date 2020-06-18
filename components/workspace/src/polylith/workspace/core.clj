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

(defn enrich-component [top-ns interface-names {:keys [name type src-namespaces test-namespaces interface] :as component}]
  (let [interface-deps (deps/brick-interface-deps top-ns interface-names component)
        src-lib-imports (lib-imports/src-lib-imports top-ns interface-names component)
        test-lib-imports (lib-imports/test-lib-imports top-ns interface-names component)]
    (array-map :name name
               :type type
               :lines-of-code-src (brick-loc src-namespaces)
               :lines-of-code-test (brick-loc test-namespaces)
               :interface interface
               :src-namespaces src-namespaces
               :test-namespaces test-namespaces
               :src-lib-imports src-lib-imports
               :test-lib-imports test-lib-imports
               :interface-deps interface-deps)))

(defn enrich-base [top-ns interface-names {:keys [name type src-namespaces test-namespaces] :as base}]
  (let [interface-deps (deps/brick-interface-deps top-ns interface-names base)
        src-lib-imports (lib-imports/src-lib-imports top-ns interface-names base)
        test-lib-imports (lib-imports/test-lib-imports top-ns interface-names base)]
    (array-map :name name
               :type type
               :lines-of-code-src (brick-loc src-namespaces)
               :lines-of-code-test (brick-loc test-namespaces)
               :src-namespaces src-namespaces
               :test-namespaces test-namespaces
               :src-lib-imports src-lib-imports
               :test-lib-imports test-lib-imports
               :interface-deps interface-deps)))

(defn enrich-env [{:keys [name group test? type components bases paths deps]}
                  brick->src-lib-imports
                  brick->test-lib-imports
                  brick->loc]
  (let [brick-names (concat components bases)
        src-lib-imports (mapcat brick->src-lib-imports brick-names)
        test-lib-imports (mapcat brick->test-lib-imports brick-names)
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
                      :src-lib-imports (vec (sort (set src-lib-imports)))
                      :test-lib-imports (vec (sort (set test-lib-imports)))
                      :deps deps)))

(defn brick->src-lib-imports [brick]
  (into {} (mapv (juxt :name :src-lib-imports) brick)))

(defn brick->test-lib-imports [brick]
  (into {} (mapv (juxt :name :test-lib-imports) brick)))

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
        brick->src-lib-imports (brick->src-lib-imports enriched-bricks)
        brick->test-lib-imports (brick->test-lib-imports enriched-bricks)
        brick->loc (into {} (map (juxt :name #(select-keys % [:lines-of-code-src :lines-of-code-test])) enriched-bricks))
        enriched-environments (mapv #(enrich-env % brick->src-lib-imports brick->test-lib-imports brick->loc) environments)
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

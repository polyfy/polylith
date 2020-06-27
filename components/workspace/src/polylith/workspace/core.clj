(ns polylith.workspace.core
  (:require [clojure.tools.deps.alpha.util.maven :as mvn]
            [polylith.common.interface :as common]
            [polylith.deps.interface :as deps]
            [polylith.validate.interface :as validate]
            [polylith.workspace.calculate-interfaces :as ifcs]
            [polylith.workspace.lib-imports :as lib]
            [polylith.util.interface :as util]
            [polylith.file.interface :as file]))

(defn brick-loc [namespaces]
  (apply + (mapv file/lines-of-code
                 (mapv :file-path namespaces))))

(defn env-loc [brick-names brick->loc test?]
  (let [locs (map brick->loc brick-names)]
    (if test?
      (apply + (filter identity (map :lines-of-code-test locs)))
      (apply + (filter identity (map :lines-of-code-src locs))))))

(defn enrich-component [top-ns interface-names {:keys [name type namespaces-src namespaces-test interface] :as component}]
  (let [interface-deps (deps/brick-interface-deps top-ns interface-names component)
        lib-imports-src (lib/lib-imports-src top-ns interface-names component)
        lib-imports-test (lib/lib-imports-test top-ns interface-names component)]
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
        lib-imports-src (lib/lib-imports-src top-ns interface-names base)
        lib-imports-test (lib/lib-imports-test top-ns interface-names base)]
    (array-map :name name
               :type type
               :lines-of-code-src (brick-loc namespaces-src)
               :lines-of-code-test (brick-loc namespaces-test)
               :namespaces-src namespaces-src
               :namespaces-test namespaces-test
               :lib-imports-src lib-imports-src
               :lib-imports-test lib-imports-test
               :interface-deps interface-deps)))

(defn select-lib-imports [brick-name brick->lib-imports test?]
  (let [{:keys [lib-imports-src lib-imports-test]} (brick->lib-imports brick-name)]
    (if test?
      lib-imports-test
      lib-imports-src)))

(defn env-lib-imports [brick-names brick->lib-imports test?]
  (mapcat #(select-lib-imports % brick->lib-imports test?)
          brick-names))

(defn enrich-env [{:keys [name group test? type component-names base-names paths deps maven-repos]}
                  brick->loc
                  brick->lib-imports]
  (let [brick-names (concat component-names base-names)
        lib-imports (-> (env-lib-imports brick-names brick->lib-imports test?)
                        set sort vec)
        lines-of-code (env-loc brick-names brick->loc test?)]
    (util/ordered-map :name name
                      :group group
                      :test? test?
                      :type type
                      :lines-of-code lines-of-code
                      :component-names component-names
                      :base-names base-names
                      :paths paths
                      :lib-imports lib-imports
                      :deps deps
                      :maven-repos maven-repos)))

(defn brick->lib-imports [brick]
  (into {} (mapv (juxt :name #(select-keys % [:lib-imports-src
                                              :lib-imports-test]))
                 brick)))

(defn brick->loc [bricks]
  (into {} (map (juxt :name #(select-keys % [:lines-of-code-src
                                             :lines-of-code-test]))
                bricks)))

(defn enrich-workspace [{:keys [ws-path settings components bases environments]}]
  (let [top-ns (common/top-namespace (:top-namespace settings))
        interfaces (ifcs/interfaces components)
        interface-names (apply sorted-set (mapv :name interfaces))
        enriched-components (mapv #(enrich-component top-ns interface-names %) components)
        enriched-bases (mapv #(enrich-base top-ns interface-names %) bases)
        enriched-bricks (concat enriched-components enriched-bases)
        brick->loc (brick->loc enriched-bricks)
        brick->lib-imports (brick->lib-imports enriched-bricks)
        enriched-environments (mapv #(enrich-env % brick->loc brick->lib-imports) environments)
        dark-mode? (:dark-mode? settings false)
        messages (validate/messages top-ns interface-names interfaces enriched-components enriched-bases enriched-environments dark-mode?)]
    (array-map :ws-path ws-path
               :settings settings
               :interfaces interfaces
               :components enriched-components
               :bases enriched-bases
               :environments enriched-environments
               :messages messages)))

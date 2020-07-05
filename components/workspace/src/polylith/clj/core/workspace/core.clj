(ns polylith.clj.core.workspace.core
  (:require [clojure.string :as str]
            [polylith.clj.core.common.interfc :as common]
            [polylith.clj.core.util.interfc.color :as color]
            [polylith.clj.core.deps.interfc :as deps]
            [polylith.clj.core.validate.interfc :as validate]
            [polylith.clj.core.workspace.calculate-interfaces :as ifcs]
            [polylith.clj.core.workspace.lib-imports :as lib]
            [polylith.clj.core.workspace.alias :as alias]
            [polylith.clj.core.util.interfc :as util]
            [polylith.clj.core.file.interfc :as file]))

(defn brick-loc [namespaces]
  (apply + (mapv file/lines-of-code
                 (mapv :file-path namespaces))))

(defn env-loc [brick-names brick->loc test?]
  (let [locs (map brick->loc brick-names)]
    (if test?
      (apply + (filter identity (map :lines-of-code-test locs)))
      (apply + (filter identity (map :lines-of-code-src locs))))))

(defn enrich-component
  ([interface-names name type top-namespace namespaces-src namespaces-test interface component]
   (let [top-ns (common/top-namespace top-namespace)
         interface-deps (deps/brick-interface-deps top-ns interface-names component)
         lib-imports-src (lib/lib-imports-src top-ns interface-names component)
         lib-imports-test (lib/lib-imports-test top-ns interface-names component)]
     (array-map :name name
                :type type
                :top-namespace top-namespace
                :lines-of-code-src (brick-loc namespaces-src)
                :lines-of-code-test (brick-loc namespaces-test)
                :interface interface
                :namespaces-src namespaces-src
                :namespaces-test namespaces-test
                :lib-imports-src lib-imports-src
                :lib-imports-test lib-imports-test
                :interface-deps interface-deps)))
  ([interface-names {:keys [name type top-namespace top-namespaces namespaces-src namespaces-test interface] :as component}]
   (if top-namespace
     (enrich-component interface-names name type top-namespace namespaces-src namespaces-test interface component)
     (array-map :name name
                :type type
                :top-namespaces top-namespaces))))

(defn enrich-base
  ([interface-names name type top-namespace namespaces-src namespaces-test base]
   (let [interface-deps (deps/brick-interface-deps top-namespace interface-names base)
         lib-imports-src (lib/lib-imports-src top-namespace interface-names base)
         lib-imports-test (lib/lib-imports-test top-namespace interface-names base)]
     (array-map :name name
                :type type
                :top-namespace top-namespace
                :lines-of-code-src (brick-loc namespaces-src)
                :lines-of-code-test (brick-loc namespaces-test)
                :namespaces-src namespaces-src
                :namespaces-test namespaces-test
                :lib-imports-src lib-imports-src
                :lib-imports-test lib-imports-test
                :interface-deps interface-deps)))
  ([interface-names {:keys [name type top-namespace top-namespaces namespaces-src namespaces-test] :as base}]
   (if top-namespace
     (enrich-base interface-names name type top-namespace namespaces-src namespaces-test base)
     (array-map :name name
                :type type
                :top-namespaces top-namespaces))))

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
                  brick->lib-imports
                  env->alias]
  (let [brick-names (concat component-names base-names)
        lib-imports (-> (env-lib-imports brick-names brick->lib-imports test?)
                        set sort vec)
        lines-of-code (env-loc brick-names brick->loc test?)]
    (util/ordered-map :name name
                      :group group
                      :test? test?
                      :alias (env->alias name)
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

(defn workspace-name [ws-path]
  (let [cleaned-ws-path (if (= "." ws-path) "" ws-path)
        path (file/absolute-path cleaned-ws-path)
        index (str/last-index-of path "/")]
    (if (>= index 0)
      (subs path (inc index))
      path)))

(defn enrich-workspace [{:keys [ws-path settings components bases environments]}]
  (let [ws-name (workspace-name ws-path)
        interfaces (ifcs/interfaces components)
        interface-names (apply sorted-set (mapv :name interfaces))
        enriched-components (mapv #(enrich-component interface-names %) components)
        enriched-bases (mapv #(enrich-base interface-names %) bases)
        enriched-bricks (concat enriched-components enriched-bases)
        lines-of-code-src (apply + (filter identity (map :lines-of-code-src enriched-bricks)))
        lines-of-code-test (apply + (filter identity (map :lines-of-code-test enriched-bricks)))
        brick->loc (brick->loc enriched-bricks)
        brick->lib-imports (brick->lib-imports enriched-bricks)
        env->alias (alias/env->alias settings environments)
        enriched-environments (vec (sort-by :name (map #(enrich-env % brick->loc brick->lib-imports env->alias) environments)))
        color-mode (:color-mode settings color/none)
        top-namespaces (map key (:top-namespaces settings))
        messages (validate/messages interface-names interfaces enriched-components enriched-bases enriched-environments top-namespaces color-mode)]
    (array-map :name ws-name
               :ws-path ws-path
               :settings settings
               :interfaces interfaces
               :components enriched-components
               :bases enriched-bases
               :environments enriched-environments
               :lines-of-code-src lines-of-code-src
               :lines-of-code-test lines-of-code-test
               :messages messages)))

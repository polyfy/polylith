(ns polylith.clj.core.workspace.core
  (:require [clojure.string :as str]
            [clojure.walk :as walk]
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

(defn enrich-workspace [{:keys [ws-path ws-reader settings components bases environments]}]
  (let [ws-name (workspace-name ws-path)
        top-ns (common/top-namespace (:top-namespace settings))
        interfaces (ifcs/interfaces components)
        interface-names (apply sorted-set (mapv :name interfaces))
        enriched-components (mapv #(enrich-component top-ns interface-names %) components)
        enriched-bases (mapv #(enrich-base top-ns interface-names %) bases)
        enriched-bricks (concat enriched-components enriched-bases)
        lines-of-code-src (apply + (filter identity (map :lines-of-code-src enriched-bricks)))
        lines-of-code-test (apply + (filter identity (map :lines-of-code-test enriched-bricks)))
        brick->loc (brick->loc enriched-bricks)
        brick->lib-imports (brick->lib-imports enriched-bricks)
        env->alias (alias/env->alias settings environments)
        enriched-environments (vec (sort-by :name (map #(enrich-env % brick->loc brick->lib-imports env->alias) environments)))
        color-mode (:color-mode settings color/none)
        messages (validate/messages top-ns interface-names interfaces enriched-components enriched-bases enriched-environments color-mode)]
    (array-map :name ws-name
               :ws-path ws-path
               :ws-reader ws-reader
               :settings settings
               :interfaces interfaces
               :components enriched-components
               :bases enriched-bases
               :environments enriched-environments
               :lines-of-code-src lines-of-code-src
               :lines-of-code-test lines-of-code-test
               :messages messages)))

(defn enrich-workspace-str-keys [workspace]
  (-> workspace walk/keywordize-keys enrich-workspace walk/stringify-keys))

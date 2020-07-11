(ns polylith.clj.core.workspace.core
  (:require [clojure.string :as str]
            [clojure.walk :as walk]
            [polylith.clj.core.common.interfc :as common]
            [polylith.clj.core.util.interfc.color :as color]
            [polylith.clj.core.validate.interfc :as validate]
            [polylith.clj.core.workspace.base :as base]
            [polylith.clj.core.workspace.component :as component]
            [polylith.clj.core.workspace.brick-deps :as brick-deps]
            [polylith.clj.core.workspace.interfaces :as interfaces]
            [polylith.clj.core.workspace.environment :as env]
            [polylith.clj.core.workspace.alias :as alias]
            [polylith.clj.core.file.interfc :as file]))

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
        interfaces (interfaces/calculate components)
        interface-names (apply sorted-set (mapv :name interfaces))
        enriched-components (mapv #(component/enrich top-ns interface-names %) components)
        enriched-bases (mapv #(base/enrich top-ns interface-names %) bases)
        enriched-bricks (concat enriched-components enriched-bases)
        lines-of-code-src (apply + (filter identity (map :lines-of-code-src enriched-bricks)))
        lines-of-code-test (apply + (filter identity (map :lines-of-code-test enriched-bricks)))
        brick->loc (brick->loc enriched-bricks)
        brick->lib-imports (brick->lib-imports enriched-bricks)
        env->alias (alias/env->alias settings environments)
        env->brick-deps (brick-deps/env->brick-deps environments enriched-components enriched-bases)
        enriched-environments (vec (sort-by :name (map #(env/enrich-env % brick->loc brick->lib-imports env->alias env->brick-deps) environments)))
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

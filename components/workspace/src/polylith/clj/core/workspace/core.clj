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

(defn workspace-name [ws-dir]
  (let [cleaned-ws-dir (if (= "." ws-dir) "" ws-dir)
        path (file/absolute-path cleaned-ws-dir)
        index (str/last-index-of path "/")]
    (if (>= index 0)
      (subs path (inc index))
      path)))

(defn enrich-workspace [{:keys [ws-dir ws-reader settings components bases environments]}]
  (let [ws-name (workspace-name ws-dir)
        {:keys [top-namespace interface-ns color-mode ns->lib]} settings
        suffixed-top-ns (common/suffix-ns-with-dot top-namespace)
        interfaces (interfaces/calculate components)
        interface-names (apply sorted-set (mapv :name interfaces))
        enriched-components (mapv #(component/enrich suffixed-top-ns interface-names %) components)
        enriched-bases (mapv #(base/enrich suffixed-top-ns interface-names %) bases)
        enriched-bricks (concat enriched-components enriched-bases)
        total-loc-src-bricks (apply + (filter identity (map :lines-of-code-src enriched-bricks)))
        total-loc-test-bricks (apply + (filter identity (map :lines-of-code-test enriched-bricks)))
        brick->loc (brick->loc enriched-bricks)
        brick->lib-imports (brick->lib-imports enriched-bricks)
        env->alias (alias/env->alias settings environments)
        env->brick-deps (brick-deps/env->brick-deps environments enriched-components enriched-bases)
        enriched-environments (vec (sort-by :name (map #(env/enrich-env % brick->loc brick->lib-imports env->alias env->brick-deps) environments)))
        total-loc-src-env (apply + (filter identity (map :lines-of-code-src enriched-environments)))
        total-loc-test-env (apply + (filter identity (map :lines-of-code-test enriched-environments)))
        messages (validate/messages ws-dir top-namespace suffixed-top-ns interface-names interfaces enriched-components enriched-bases enriched-environments interface-ns ns->lib color-mode)]
    (array-map :name ws-name
               :ws-dir ws-dir
               :ws-reader ws-reader
               :settings settings
               :interfaces interfaces
               :components enriched-components
               :bases enriched-bases
               :environments enriched-environments
               :total-loc-src-bricks total-loc-src-bricks
               :total-loc-test-bricks total-loc-test-bricks
               :total-loc-src-environments total-loc-src-env
               :total-loc-test-environments total-loc-test-env
               :messages messages)))

(defn enrich-workspace-str-keys [workspace]
  (-> workspace walk/keywordize-keys enrich-workspace walk/stringify-keys))

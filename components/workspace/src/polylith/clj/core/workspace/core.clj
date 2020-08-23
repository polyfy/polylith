(ns polylith.clj.core.workspace.core
  (:require [clojure.string :as str]
            [clojure.walk :as walk]
            [polylith.clj.core.common.interfc :as common]
            [polylith.clj.core.validator.interfc :as validator]
            [polylith.clj.core.workspace.alias :as alias]
            [polylith.clj.core.workspace.base :as base]
            [polylith.clj.core.workspace.component :as component]
            [polylith.clj.core.workspace.environment :as env]
            [polylith.clj.core.workspace.interfaces :as interfaces]
            [polylith.clj.core.workspace.user-input :as user-input]
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

(defn env-sorter [{:keys [dev? name]}]
  [dev? name])

(defn enrich-workspace [{:keys [ws-dir ws-reader settings components bases environments]}
                        user-input]
  (let [ws-name (workspace-name ws-dir)
        {:keys [top-namespace interface-ns ns->lib color-mode]} settings
        suffixed-top-ns (common/suffix-ns-with-dot top-namespace)
        interfaces (interfaces/calculate components)
        interface-names (apply sorted-set (mapv :name interfaces))
        enriched-components (mapv #(component/enrich suffixed-top-ns interface-names settings %) components)
        enriched-bases (mapv #(base/enrich suffixed-top-ns interface-names settings %) bases)
        enriched-bricks (concat enriched-components enriched-bases)
        brick->loc (brick->loc enriched-bricks)
        brick->lib-imports (brick->lib-imports enriched-bricks)
        env->alias (alias/env->alias settings environments)
        enriched-environments (vec (sort-by env-sorter (map #(env/enrich-env % ws-dir enriched-components enriched-bases brick->loc brick->lib-imports env->alias settings user-input) environments)))
        enriched-user-input (user-input/enrich settings user-input)
        messages (validator/validate-ws ws-dir suffixed-top-ns settings interface-names interfaces enriched-components enriched-bases enriched-environments interface-ns ns->lib color-mode)]
    (array-map :name ws-name
               :ws-dir ws-dir
               :user-input enriched-user-input
               :ws-reader ws-reader
               :settings settings
               :interfaces interfaces
               :components enriched-components
               :bases enriched-bases
               :environments enriched-environments
               :messages messages)))

(defn enrich-workspace-str-keys [workspace user-input]
  (-> workspace walk/keywordize-keys (enrich-workspace user-input) walk/stringify-keys))

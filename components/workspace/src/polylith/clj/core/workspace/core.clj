(ns polylith.clj.core.workspace.core
  (:require [clojure.string :as str]
            [polylith.clj.core.file.interface :as file]
            [polylith.clj.core.common.interface :as common]
            [polylith.clj.core.validator.interface :as validator]
            [polylith.clj.core.workspace.alias :as alias]
            [polylith.clj.core.workspace.base :as base]
            [polylith.clj.core.workspace.component :as component]
            [polylith.clj.core.workspace.environment :as env]
            [polylith.clj.core.workspace.settings :as settings]
            [polylith.clj.core.workspace.interfaces :as interfaces]
            [polylith.clj.core.workspace.user-input :as user-input]))

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
        index (str/last-index-of path file/sep)]
    (if (>= index 0)
      (subs path (inc index))
      path)))

(defn env-sorter [{:keys [dev? name]}]
  [dev? name])

(defn enrich-workspace [{:keys [ws-dir settings components bases environments paths] :as workspace}]
  (let [ws-name (workspace-name ws-dir)
        {:keys [top-namespace interface-ns user-input color-mode]} settings
        suffixed-top-ns (common/suffix-ns-with-dot top-namespace)
        interfaces (interfaces/calculate components)
        interface-names (apply sorted-set (mapv :name interfaces))
        enriched-components (mapv #(component/enrich suffixed-top-ns interface-names settings %) components)
        enriched-bases (mapv #(base/enrich suffixed-top-ns interface-names settings %) bases)
        enriched-bricks (concat enriched-components enriched-bases)
        brick->loc (brick->loc enriched-bricks)
        brick->lib-imports (brick->lib-imports enriched-bricks)
        env->alias (alias/env->alias settings environments)
        enriched-user-input (user-input/enrich settings user-input)
        enriched-settings (settings/enrich ws-dir settings)
        enriched-environments (vec (sort-by env-sorter (map #(env/enrich-env % enriched-components enriched-bases brick->loc brick->lib-imports env->alias paths settings enriched-user-input) environments)))
        messages (validator/validate-ws suffixed-top-ns settings paths interface-names interfaces enriched-components enriched-bases enriched-environments interface-ns enriched-user-input color-mode)]
    (assoc workspace :name ws-name
                     :user-input enriched-user-input
                     :settings enriched-settings
                     :interfaces interfaces
                     :components enriched-components
                     :bases enriched-bases
                     :environments enriched-environments
                     :messages messages)))

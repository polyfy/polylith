(ns polylith.clj.core.workspace.core
  (:require [polylith.clj.core.common.interface :as common]
            [polylith.clj.core.validator.interface :as validator]
            [polylith.clj.core.workspace.settings :as s]
            [polylith.clj.core.workspace.base :as base]
            [polylith.clj.core.workspace.component :as component]
            [polylith.clj.core.workspace.project :as project]
            [polylith.clj.core.workspace.interfaces :as interfaces]))

(defn brick->lib-imports [bricks]
  (into {} (map (juxt :name :lib-imports)) bricks))

(defn brick->loc [bricks]
  (into {} (map (juxt :name :lines-of-code)) bricks))

(defn project-sorter [{:keys [is-dev name]}]
  [is-dev name])

(defn enrich-workspace [{:keys [ws-dir user-input settings components bases config-error projects paths] :as workspace}]
  (if (or (nil? workspace)
          config-error)
    workspace
    (let [{:keys [top-namespace interface-ns color-mode]} settings
          suffixed-top-ns (common/suffix-ns-with-dot top-namespace)
          interfaces (interfaces/calculate components)
          interface-names (into (sorted-set) (keep :name) interfaces)
          enriched-components (mapv #(component/enrich ws-dir suffixed-top-ns interface-names %) components)
          enriched-bases (mapv #(base/enrich ws-dir suffixed-top-ns interface-names %) bases)
          enriched-bricks (into [] cat [enriched-components enriched-bases])
          brick->loc (brick->loc enriched-bricks)
          brick->lib-imports (brick->lib-imports enriched-bricks)
          enriched-settings (s/enrich-settings settings projects)
          enriched-projects (vec (sort-by project-sorter (mapv #(project/enrich-project % ws-dir enriched-components enriched-bases suffixed-top-ns brick->loc brick->lib-imports paths enriched-settings) projects)))
          messages (validator/validate-ws suffixed-top-ns enriched-settings paths interface-names interfaces enriched-components enriched-bases enriched-projects interface-ns user-input color-mode)]
      (assoc workspace :settings enriched-settings
                       :interfaces interfaces
                       :components enriched-components
                       :bases enriched-bases
                       :projects enriched-projects
                       :messages messages))))

(ns ^:no-doc polylith.clj.core.workspace.enrich.core
  (:require [polylith.clj.core.antq.ifc :as antq]
            [polylith.clj.core.common.interface :as common]
            [polylith.clj.core.lib.interface :as lib]
            [polylith.clj.core.validator.interface :as validator]
            [polylith.clj.core.workspace.enrich.base :as base]
            [polylith.clj.core.workspace.enrich.component :as component]
            [polylith.clj.core.workspace.enrich.test-configs :as test-configs]
            [polylith.clj.core.workspace.enrich.profile :as profile]
            [polylith.clj.core.workspace.enrich.project :as project]
            [polylith.clj.core.interface.interface :as interface]))

(defn brick->lib-imports [bricks]
  (into {} (map (juxt :name :lib-imports)) bricks))

(defn brick->loc [bricks]
  (into {} (map (juxt :name :lines-of-code)) bricks))

(defn project-sorter [{:keys [is-dev name]}]
  [is-dev name])

(defn ->name-type->keep-lib-versions [bases components projects]
  (into {}
        (mapv (juxt #(vector (:name %) (:type %)) :keep-lib-versions)
              (concat bases components projects))))

(defn enrich-workspace [{:keys [ws-dir user-input settings configs components bases profiles config-errors projects paths] :as workspace}
                        workspaces]
  (if (common/invalid-workspace? workspace)
    workspace
    (let [{:keys [top-namespace interface-ns test color-mode]} settings
          suffixed-top-ns (common/suffix-ns-with-dot top-namespace)
          interfaces (interface/calculate components)
          interface-names (into (sorted-set) (keep :name) interfaces)
          calculate-latest-version? (common/calculate-latest-version? user-input)
          library->latest-version (antq/library->latest-version configs calculate-latest-version?)
          outdated-libs (lib/outdated-libs library->latest-version)
          name-type->keep-lib-versions (->name-type->keep-lib-versions bases components projects)
          enriched-components (mapv #(component/enrich ws-dir suffixed-top-ns interface-names outdated-libs library->latest-version user-input name-type->keep-lib-versions workspaces %)
                                    components)
          enriched-bases (mapv #(base/enrich ws-dir suffixed-top-ns bases interface-names outdated-libs library->latest-version user-input name-type->keep-lib-versions workspaces %)
                               bases)
          enriched-bricks (into [] cat [enriched-components enriched-bases])
          brick->loc (brick->loc enriched-bricks)
          brick->lib-imports (brick->lib-imports enriched-bricks)
          alias-id (atom 0)
          enriched-projects (vec (sort-by project-sorter
                                          (mapv #(project/enrich-project % ws-dir alias-id enriched-components enriched-bases profiles suffixed-top-ns brick->loc brick->lib-imports paths user-input configs settings workspaces name-type->keep-lib-versions outdated-libs library->latest-version)
                                                projects)))
          enriched-profiles (profile/enrich profiles configs workspaces)
          enriched-settings (test-configs/with-configs settings test configs user-input)
          messages (validator/validate-ws suffixed-top-ns settings paths interface-names interfaces profiles enriched-components enriched-bases enriched-projects workspaces config-errors interface-ns user-input color-mode)]
      (cond-> workspace
              true (assoc :interfaces interfaces
                          :components enriched-components
                          :bases enriched-bases
                          :projects enriched-projects
                          :profiles enriched-profiles
                          :settings enriched-settings
                          :messages messages)
              true (dissoc :config-errors)
              (seq workspaces) (assoc :workspaces workspaces)))))

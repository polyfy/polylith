(ns dev.development
  (:require [dev.dev-common :as dev-common]
            [polylith.clj.core.lib.text-table.lib-table :as lib-table]
            [polylith.clj.core.command.info :as info]
            [polylith.clj.core.common.interface :as common]
            [polylith.clj.core.change.interface :as change]
            [polylith.clj.core.workspace-clj.interface :as ws-clj]
            [polylith.clj.core.deps.text-table.workspace-deps-table :as ws-ifc-deps-table]
            [polylith.clj.core.deps.text-table.project-deps-table :as ws-project-deps-table]
            [polylith.clj.core.deps.text-table.brick-deps-table :as brick-ifc-deps-table]
            [polylith.clj.core.deps.text-table.project-brick-deps-table :as brick-deps-table]
            [polylith.clj.core.workspace.interface :as ws]))

(def workspace (-> (dev-common/dir ".")
                   ws-clj/workspace-from-disk
                   ws/enrich-workspace
                   change/with-changes))

;; info
(info/info workspace [])

;; libs
(lib-table/print-table workspace false)

;; deps
(ws-ifc-deps-table/print-table workspace)
(ws-project-deps-table/print-table workspace "poly" false)
(brick-ifc-deps-table/print-table workspace "workspace")
(brick-deps-table/print-table workspace "poly" "workspace")

(def settings (:settings workspace))
(def components (:components workspace))
(def bases (:bases workspace))
(def component (common/find-component "workspace" components))
(def projects (:projects workspace))
(def base (common/find-base "poly-cli" bases))
(def project (common/find-project "development" projects))
(def project (common/find-project "poly" projects))

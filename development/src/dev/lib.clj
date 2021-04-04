(ns dev.lib
  (:require [dev.dev-common :as dev-common]
            [polylith.clj.core.lib.text-table.lib-table :as lib-table]
            [polylith.clj.core.change.interface :as change]
            [polylith.clj.core.workspace-clj.interface :as ws-clj]
            [polylith.clj.core.workspace.interface :as ws]))

(def workspace (-> (dev-common/dir "example/output/example")
                   ws-clj/workspace-from-disk
                   ws/enrich-workspace
                   change/with-changes))

(lib-table/print-table workspace false)

(def settings (:settings workspace))

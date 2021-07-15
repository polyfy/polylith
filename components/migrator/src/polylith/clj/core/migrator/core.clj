(ns polylith.clj.core.migrator.core
  (:require [polylith.clj.core.common.interface :as common]
            [polylith.clj.core.migrator.brick-deps :as brick-deps]
            [polylith.clj.core.migrator.dev-deps :as dev-deps]
            [polylith.clj.core.migrator.project-deps :as project-deps]
            [polylith.clj.core.migrator.workspace-deps :as workspace-deps]))

(defn migrate
  "Migrates from the old :toolsdeps1 format to the new :toolsdeps2 format,
   where bricks have their own 'deps.edn' config file and where workspace
   settings is stored in workspace.edn instead of :polylith key in ./deps.edn.
   0.1.0-alpha9 is the last version that supports the old :toolsdeps1 format.
   0.2.0-alpha10 is the first version that supports the :toolsdeps2 format."
  [ws-dir workspace]
  (if (common/toolsdeps1? workspace)
    (do
      (workspace-deps/create-config-file ws-dir workspace)
      (brick-deps/create-config-files ws-dir workspace)
      (dev-deps/recreate-config-file ws-dir)
      (project-deps/recreate-config-files ws-dir workspace)
      (println "  Finished migration."))
    (println "  Workspace already migrated.")))

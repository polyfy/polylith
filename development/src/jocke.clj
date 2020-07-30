(ns jocke
  (:require [clojure.string :as str]
            [polylith.clj.core.workspace.interfc :as ws]
            [polylith.clj.core.change.interfc :as change]
            [polylith.clj.core.util.interfc :as util]
            [polylith.clj.core.workspace-clj.interfc :as ws-clj]
            [polylith.clj.core.file.interfc :as file]
            [polylith.clj.core.help.interfc :as help]))

(comment

  (def workspace (->
                   "."
                   ;"../clojure-polylith-realworld-example-app"
                   ws-clj/workspace-from-disk
                   ws/enrich-workspace
                   change/with-changes))

  ;(require '[polylith.clj.core.z-jocke.api :as z])
  ;(def workspace z/workspace)

  (def environments (:environments workspace))
  (def components (:components workspace))
  (def bases (:bases workspace))
  (def bricks (concat components bases))

  (def interfaces (:interfaces workspace))
  (def changes (:changes workspace))

  (def environment (util/find-first #(= "core" (:name %)) environments))
  (def dev (util/find-first #(= "dev" (:name %)) environments))
  (def cli (util/find-first #(= "cli" (:name %)) environments))

  (map (juxt :name :paths) environments)


  (def component (util/find-first #(= "workspace" (:name %)) components))


  (map :name components)


  (def workspace (-> "."
                     ws-clj/workspace-from-disk
                     ws/enrich-workspace
                     change/with-changes))

  (def environments (:environments workspace))
  (def environment (util/find-first #(= "dev2" (:name %)) environments))

  (def src-paths (sort (set (concat (:paths environment) (:test-paths environment)))))
  (apply repl/set-refresh-dirs src-paths)
  ;(repl/refresh)
  (repl/refresh-all)

  (env-reloader/reload "dev2")

  (println "################################")

  (println (str/join "\n" (sort (str/split "polylith.clj.core.file.core polylith.clj.core.file.interfc polylith.clj.core.util.core polylith.clj.core.util.interfc.color polylith.clj.core.common.message polylith.clj.core.util.interfc polylith.clj.core.common.class-loader polylith.clj.core.common.core polylith.clj.core.common.interfc polylith.clj.core.util.str polylith.clj.core.util.interfc.str polylith.clj.core.workspace-clj.namespaces-from-disk polylith.clj.core.workspace-clj.bases-from-disk polylith.clj.core.workspace-clj.definitions polylith.clj.core.workspace-clj.interface-defs-from-disk polylith.clj.core.workspace-clj.components-from-disk polylith.clj.core.workspace-clj.environment-from-disk polylith.clj.core.workspace-clj.core polylith.clj.core.test-runner.core polylith.clj.core.validate.shared polylith.clj.core.util.interfc.exception polylith.clj.core.workspace.alias polylith.clj.core.text-table.line polylith.clj.core.workspace.loc polylith.clj.core.deps.brick-deps polylith.clj.core.text-table.orientation polylith.clj.core.text-table.core polylith.clj.core.text-table.interfc polylith.clj.core.deps.text-table.workspace-brick-deps-table polylith.clj.core.deps.text-table.brick-deps-table polylith.clj.core.deps.text-table.workspace-ifc-deps-table polylith.clj.core.deps.text-table.brick-ifc-deps-table polylith.clj.core.deps.interface-deps polylith.clj.core.deps.interfc polylith.clj.core.workspace.lib-imports polylith.clj.core.workspace.component polylith.clj.core.shell.core polylith.clj.core.shell.interfc polylith.clj.core.git.core polylith.clj.core.git.interfc polylith.clj.core.change.entity polylith.clj.core.validate.m102-function-or-macro-is-defined-twice polylith.clj.core.validate.m201-mismatching-parameters polylith.clj.core.validate.m107-missing-componens-in-environment polylith.clj.core.create.workspace polylith.clj.core.create.environment polylith.clj.core.create.interfc polylith.clj.core.workspace.base polylith.clj.core.workspace.interfaces polylith.clj.core.workspace.text-table.nof-bricks-table polylith.clj.core.workspace.text-table.env-table polylith.clj.core.workspace.text-table.ws-table polylith.clj.core.validate.m101-illegal-namespace-deps polylith.clj.core.workspace.brick-deps polylith.clj.core.validate.m106-multiple-interface-occurrences polylith.clj.core.workspace.environment polylith.clj.core.validate.m105-illegal-name-sharing polylith.clj.core.validate.m103-missing-defs polylith.clj.core.validate.m104-circular-deps polylith.clj.core.validate.interfc polylith.clj.core.workspace.core polylith.clj.core.user-config.interfc polylith.clj.core.workspace.interfc polylith.clj.core.help.interfc polylith.clj.core.test-runner.interfc polylith.clj.core.command.core polylith.clj.core.change.environment polylith.clj.core.change.to-test polylith.clj.core.change.indirect polylith.clj.core.change.core polylith.clj.core.change.interfc polylith.clj.core.command.interfc polylith.clj.core.workspace-clj.interfc polylith.clj.core.test-helper.core polylith.clj.core.test-helper.interfc polylith.clj.core.create.environment-test polylith.clj.core.workspace.text-table.nof-bricks-table-test polylith.clj.core.cli.poly polylith.clj.core.validate.m102-duplicated-parameter-lists-test polylith.clj.core.validate.m101-illegal-namespace-deps-test polylith.clj.core.common.path-extractor polylith.clj.core.validate.m103-missing-defs-test polylith.clj.core.util.string-util-test polylith.clj.core.deps.brick-deps-test polylith.clj.core.file.core-test polylith.clj.core.workspace-clj.readimportsfromdisk-test polylith.clj.core.change.environment-test polylith.clj.core.workspace.environment-test polylith.clj.core.text-table.interfc-test polylith.clj.core.deps.text-table.workspace-ifc-deps-table-test polylith.clj.core.deps.text-table.brick-ifc-deps-table-test polylith.clj.core.workspace.text-table.env-table-test polylith.clj.core.validate.m106-multiple-interface-occurrences-test polylith.clj.core.validate.m104-circular-deps-test polylith.clj.core.workspace-clj.import-from-disk-test polylith.clj.core.change.brick-test polylith.clj.core.deps.text-table.workspace-brick-deps-table-test polylith.clj.core.validate.m105-illegal-name-sharing-test polylith.clj.core.workspace.text-table.ws-table-test polylith.clj.core.validate.m107-missing-componens-in-environment-test polylith.clj.core.workspace.brick-deps-test polylith.clj.core.workspace.lib-imports-test polylith.clj.core.workspace-clj.environment-test polylith.clj.core.workspace-clj.definitions-test polylith.clj.core.validate.m201-mismatching-parameters-test polylith.clj.core.git.git-test polylith.clj.core.change.indirect-test polylith.clj.core.util.util-test polylith.clj.core.change.core-test polylith.clj.core.create.workspace-test polylith.clj.core.deps.interface-deps-test polylith.clj.core.deps.text-table.brick-deps-table-test polylith.clj.core.workspace.alias-test polylith.clj.core.newcomp.interfc polylith.clj.core.newcomp.interfc-test polylith.clj.core.help.interfc-test polylith.clj.core.validate.m202-missing-path-in-environment" #" "))))

  (println (str/join "\n" src-paths)))

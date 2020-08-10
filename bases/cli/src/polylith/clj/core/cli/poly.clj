(ns polylith.clj.core.cli.poly
  (:require [polylith.clj.core.change.interfc :as change]
            [polylith.clj.core.command.interfc :as command]
            [polylith.clj.core.file.interfc :as file]
            [polylith.clj.core.util.interfc.exception :as ex]
            [polylith.clj.core.util.interfc.params :as params]
            [polylith.clj.core.workspace.interfc :as ws]
            [polylith.clj.core.workspace-clj.interfc :as ws-clj])
  (:gen-class))

(defn -main [& [cmd arg1 arg2 arg3]]
  (let [current-dir (file/current-dir)
        exists? (file/exists (str current-dir "/deps.edn"))
        env (-> (params/extract arg1 arg2 arg3) :named-args :env)
        enable-dev? (contains? #{"dev" "development"} env)
        workspace (when exists? (-> current-dir
                                    ws-clj/workspace-from-disk
                                    ws/enrich-workspace
                                    (change/with-changes enable-dev?)))
        {:keys [exit-code exception]} (command/execute-command current-dir workspace cmd arg1 arg2 arg3)]
      (when (-> exception nil? not)
        (ex/print-exception exception))
      (System/exit exit-code)))

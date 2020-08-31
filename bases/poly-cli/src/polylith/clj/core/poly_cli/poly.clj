(ns polylith.clj.core.poly-cli.poly
  (:require [polylith.clj.core.change.interfc :as change]
            [polylith.clj.core.command.interfc :as command]
            [polylith.clj.core.file.interfc :as file]
            [polylith.clj.core.user-input.interfc :as user-input]
            [polylith.clj.core.util.interfc.exception :as ex]
            [polylith.clj.core.workspace.interfc :as ws]
            [polylith.clj.core.workspace-clj.interfc :as ws-clj])
  (:gen-class))

(defn ws-directory [{:keys [cmd ws-dir]}]
  (if (or (nil? ws-dir)
          (= cmd "test"))
    (file/current-dir)
    ws-dir))

(defn -main [& args]
  (let [user-input (user-input/extract-params args)
        ws-dir (ws-directory user-input)
        exists? (file/exists (str ws-dir "/deps.edn"))
        workspace (when exists? (-> ws-dir
                                    ws-clj/workspace-from-disk
                                    (ws/enrich-workspace user-input)
                                    change/with-changes))
        {:keys [exit-code exception]} (command/execute-command ws-dir workspace user-input)]
      (when (-> exception nil? not)
        (ex/print-exception exception))
      (System/exit exit-code)))

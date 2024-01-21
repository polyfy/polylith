(ns ^:no-doc polylith.clj.core.command.cmd-validator.core
  (:require [polylith.clj.core.command.cmd-validator.create :as create]
            [polylith.clj.core.command.cmd-validator.profile :as profile]
            [polylith.clj.core.command.cmd-validator.project :as project]
            [polylith.clj.core.command.cmd-validator.executable :as executable]))

(defn validate [{:keys [settings profiles projects config-error] :as workspace}
                {:keys [ws-dir ws-file cmd args name top-ns is-search-for-ws-dir selected-projects]}
                color-mode]
  (if config-error
    [false (str "  " config-error)]
    (let [messages (concat (profile/validate profiles settings color-mode)
                           (project/validate selected-projects projects color-mode))
          [ok? create-message] (create/validate workspace args name top-ns)]
      (if (empty? messages)
        (if ok?
          (executable/validate workspace cmd args ws-dir ws-file is-search-for-ws-dir)
          [false create-message])
        [false (first messages)]))))

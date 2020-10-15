(ns polylith.clj.core.command.cmd-validator.core
  (:require [polylith.clj.core.command.cmd-validator.profile :as profile]
            [polylith.clj.core.command.cmd-validator.project :as project]
            [polylith.clj.core.command.cmd-validator.executable :as executable]))

(defn validate [{:keys [settings projects] :as workspace}
                {:keys [ws-dir ws-file cmd arg1 is-search-for-ws-dir selected-projects]}
                color-mode]
  (let [messages (concat (profile/validate settings color-mode)
                         (project/validate selected-projects projects color-mode))]
    (if (empty? messages)
      (executable/validate workspace cmd arg1 ws-dir ws-file is-search-for-ws-dir)
      [false (first messages)])))

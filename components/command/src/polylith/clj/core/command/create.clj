(ns polylith.clj.core.command.create
  (:require [polylith.clj.core.command.shared :as shared]
            [polylith.clj.core.creator.interface :as creator]))

(defn create [current-dir workspace [_ entity] name top-ns interface color-mode]
  (let [ent (shared/entity->short entity)]
    (condp = ent
      "w" (creator/create-workspace current-dir name top-ns)
      "p" (when (= :ok (creator/create-project workspace name))
            (creator/print-alias-message name color-mode))
      "b" (creator/create-base workspace name)
      "c" (creator/create-component workspace name interface))))

(ns polylith.clj.core.command.create
  (:require [polylith.clj.core.common.interface :as common]
            [polylith.clj.core.creator.interface :as creator]))

(defn create [current-dir workspace
              [_ entity] name top-ns interface branch git-add? commit? color-mode]
  (let [ent (common/entity->short entity)
        git-add (if (-> git-add? nil? not)
                  git-add?
                  (-> workspace :settings :vcs :auto-add))]
    (condp = ent
      "w" (creator/create-workspace current-dir name top-ns branch git-add commit?)
      "p" (when (= :ok (creator/create-project workspace name git-add))
            (creator/print-alias-message name color-mode))
      "b" (creator/create-base workspace name git-add)
      "c" (creator/create-component workspace name interface git-add))))

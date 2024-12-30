(ns ^:no-doc polylith.clj.core.command.create
  (:require [polylith.clj.core.common.interface :as common]
            [polylith.clj.core.util.interface.color :as color]
            [polylith.clj.core.creator.interface :as creator]))

(defn print-warning [entity-short color-mode]
  (println (str "  The short form 'create " entity-short "' is " (color/error color-mode "deprecated") " and support for it will be dropped. "
                "Please use 'create " (common/entity->long entity-short) "' instead.")))

(defn create [current-dir workspace
              [_ entity] name alias dialect top-ns interface branch git-add? commit? color-mode]
  (let [ent (common/entity->short entity)
        git-add (if (-> git-add? nil? not)
                  git-add?
                  (-> workspace :settings :vcs :auto-add))]
    (if (contains? #{"b" "c" "p" "w"} entity)
      (print-warning entity color-mode))
    (condp = ent
      "w" (creator/create-workspace current-dir name top-ns dialect branch commit?)
      "p" (when (= :ok (creator/create-project workspace name dialect git-add))
            (creator/print-alias-message name alias color-mode))
      "b" (creator/create-base workspace name dialect git-add)
      "c" (creator/create-component workspace name interface dialect git-add))))

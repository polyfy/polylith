(ns ^:no-doc polylith.clj.core.command.create
  (:require [polylith.clj.core.common.interface :as common]
            [polylith.clj.core.util.interface.color :as color]
            [polylith.clj.core.creator.interface :as creator]))

(defn print-error-message [entity-short color-mode]
  (println (str "  The short form 'create " entity-short "' is " (color/error color-mode "deprecated") " and support for it will be dropped. "
                "Please use 'create " (common/entity->long entity-short) "' instead.")))

(defn create [current-dir workspace
              [_ entity] name alias top-ns interface branch git-add? commit? config-filename color-mode]
  (let [ent (common/entity->short entity)
        git-add (if (-> git-add? nil? not)
                  git-add?
                  (-> workspace :settings :vcs :auto-add))]
    (if (contains? #{"b" "c" "p" "w"} entity)
      (print-error-message entity color-mode))
    (condp = ent
      "w" (creator/create-workspace current-dir name top-ns branch commit?)
      "p" (when (= :ok (creator/create-project workspace name alias git-add))
            (creator/print-alias-message name alias config-filename color-mode))
      "b" (creator/create-base workspace name git-add)
      "c" (creator/create-component workspace name interface git-add))))

(comment
  (print-error-message "c" "dark")
  #__)
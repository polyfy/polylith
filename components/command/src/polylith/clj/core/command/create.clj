(ns polylith.clj.core.command.create
  (:require [clojure.string :as str]
            [polylith.clj.core.command.message :as command]
            [polylith.clj.core.command.shared :as shared]
            [polylith.clj.core.creator.interface :as creator]))

(def ent->name {"w" "my-workspace"
                "p" "my-project"
                "b" "my-base"
                "c" "my-component"})

(defn workspace? [entity]
  (= "w" entity))

(defn project-base-or-comp? [entity]
  (contains? #{"p" "b" "c"} entity))

(defn validate [workspace entity name top-ns]
  (cond
    (nil? entity) [false "  The first argument after 'create' is expected to be any of: w, p, b, c, workspace, project, base, component."]
    (and (nil? workspace)
         (project-base-or-comp? entity)) [false (command/cant-be-executed-outside-ws-message "create")]
    (nil? name) [false (str "  A name must be given, e.g.: create " entity " name:" (ent->name entity))]
    (and (workspace? entity)
         (-> workspace nil? not)) [false (str "  A workspace should not be created within another workspace.")]
    (and (workspace? entity)
         (str/blank? top-ns)) [false (str "  A top namespace must be given, e.g.: create " entity " name:" (ent->name entity) " top-ns:com.my-company")]
    :else [true]))

(defn create [current-dir workspace entity name top-ns interface color-mode]
  (let [ent (shared/entity->short entity)
        [ok? message] (validate workspace ent name top-ns)]
    (if ok?
      (condp = ent
        "w" (creator/create-workspace current-dir name top-ns)
        "p" (when (= :ok (creator/create-project workspace name))
              (creator/print-alias-message name color-mode))
        "b" (creator/create-base workspace name)
        "c" (creator/create-component workspace name interface))
      (println message))))

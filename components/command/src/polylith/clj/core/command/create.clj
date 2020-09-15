(ns polylith.clj.core.command.create
  (:require [polylith.clj.core.command.message :as command]
            [polylith.clj.core.creator.interface :as creator]
            [clojure.string :as str]))

(def ent->name {"w" "my-workspace"
                "e" "my-environment"
                "b" "my-base"
                "c" "my-component"})

(def entity->short {"w" "w"
                    "e" "e"
                    "b" "b"
                    "c" "c"
                    "workspace" "w"
                    "environment" "e"
                    "base" "b"
                    "component" "c"})

(defn workspace? [entity]
  (= "w" entity))

(defn env-base-or-comp? [entity]
  (contains? #{"e" "b" "c"} entity))

(defn validate [workspace entity name top-ns]
  (cond
    (nil? entity) [false "  The first argument after 'create' is expected to be any of: w, e, b, c, workspace, environment, base, component."]
    (and (nil? workspace)
         (env-base-or-comp? entity)) [false command/cant-be-executed-outside-ws-message]
    (nil? name) [false (str "  A name must be given, e.g.: create " entity " name:" (ent->name entity))]
    (and (workspace? entity)
         (-> workspace nil? not)) [false (str "  A workspace should not be created within another workspace.")]
    (and (workspace? entity)
         (str/blank? top-ns)) [false (str "  A top namespace must be given, e.g.: create " entity " name:" (ent->name entity) " top-ns:com.my-company")]
    :else [true]))

(defn create [current-dir workspace entity name top-ns interface color-mode]
  (let [ent (entity->short entity)
        [ok? message] (validate workspace ent name top-ns)]
    (if ok?
      (condp = ent
        "w" (creator/create-workspace current-dir name top-ns)
        "e" (when (= :ok (creator/create-environment workspace name))
              (creator/print-alias-message name color-mode))
        "b" (creator/create-base workspace name)
        "c" (creator/create-component workspace name interface))
      (println message))))

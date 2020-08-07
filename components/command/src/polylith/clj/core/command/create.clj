(ns polylith.clj.core.command.create
  (:require [polylith.clj.core.command.command :as command]
            [polylith.clj.core.create.interfc :as create]
            [polylith.clj.core.util.interfc.params :as params]))

(def ent->name {"w" "my-workspace"
                "e" "my-entity"
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
    (nil? entity) {:message "  Expected the first argument after 'create' to be any of: w, e, b, c, workspace, environment, base, component."}
    (and (nil? workspace)
         (env-base-or-comp? entity)) (command/print-outside-ws-message)
    (nil? name) {:message (str "  A name must be given, e.g.: create " entity " name:" (ent->name entity))}
    (and (workspace? entity)
         (-> workspace nil? not)) {:message (str "  A workspace should not be created within another workspace.")}
    (and (workspace? entity)
         (nil? top-ns)) {:message (str "  A top namespace must be given, e.g.: create " entity " name:" (ent->name entity) " top-ns:com.my-company")}
    :else {:ok? true}))

(defn create [current-dir workspace entity arg2 arg3]
  (let [color-mode (-> workspace :settings :color-mode)
        {:keys [named-args]} (params/parse arg2 arg3)
        {:keys [name top-ns interface]} named-args
        ent (entity->short entity)
        {:keys [ok? message]} (validate workspace ent name top-ns)]
    (if ok?
      (condp = ent
        "w" (create/create-workspace current-dir name top-ns)
        "e" (when (= :ok (create/create-environment workspace name))
              (create/print-alias-message name color-mode))
        "b" (create/create-base workspace name)
        "c" (create/create-component workspace name interface))
      (println message))))

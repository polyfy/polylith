(ns polylith.clj.core.command.create
  (:require [polylith.clj.core.command.message :as command]
            [polylith.clj.core.create.interfc :as create]))

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
    (nil? entity) {:message "  The first argument after 'create' is expected to be any of: w, e, b, c, workspace, environment, base, component."}
    (and (nil? workspace)
         (env-base-or-comp? entity)) (command/print-dont-execute-outside-ws)
    (nil? name) {:message (str "  A name must be given, e.g.: create " entity " name:" (ent->name entity))}
    (and (workspace? entity)
         (-> workspace nil? not)) {:message (str "  A workspace should not be created within another workspace.")}
    (and (workspace? entity)
         (nil? top-ns)) {:message (str "  A top namespace must be given, e.g.: create " entity " name:" (ent->name entity) " top-ns:com.my-company")}
    :else {:ok? true}))

(defn create [current-dir workspace entity name top-ns interface color-mode]
  (let [ent (entity->short entity)
        {:keys [ok? message]} (validate workspace ent name top-ns)]
    (if ok?
      (condp = ent
        "w" (create/create-workspace current-dir name top-ns)
        "e" (when (= :ok (create/create-environment workspace name))
              (create/print-alias-message name color-mode))
        "b" (create/create-base workspace name)
        "c" (create/create-component workspace name interface))
      (println message))))

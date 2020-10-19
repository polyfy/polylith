(ns polylith.clj.core.command.cmd-validator.create
  (:require [clojure.string :as str]
            [polylith.clj.core.command.message :as command]
            [polylith.clj.core.command.shared :as shared]))

(def ent->name {"w" "my-workspace"
                "p" "my-project"
                "b" "my-base"
                "c" "my-component"})

(defn workspace? [entity]
  (= "w" entity))

(defn project-base-or-comp? [entity]
  (contains? #{"p" "b" "c"} entity))

(defn validate [workspace [cmd entity] name top-ns]
  (let [ent (shared/entity->short entity)]
    (if (= "create" cmd)
      (cond
        (nil? ent) [false "  The first argument after 'create' is expected to be any of: w, p, b, c, workspace, project, base, component."]
        (and (nil? workspace)
             (project-base-or-comp? ent)) [false (command/cant-be-executed-outside-ws-message "create")]
        (nil? name) [false (str "  A name must be given, e.g.: create " ent " name:" (ent->name ent))]
        (and (workspace? ent)
             (-> workspace nil? not)) [false (str "  A workspace should not be created within another workspace.")]
        (and (workspace? ent)
             (str/blank? top-ns)) [false (str "  A top namespace must be given, e.g.: create " ent " name:" (ent->name ent) " top-ns:com.my-company")]
        :else [true])
      [true])))

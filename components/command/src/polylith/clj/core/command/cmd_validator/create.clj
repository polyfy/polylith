(ns polylith.clj.core.command.cmd-validator.create
  (:require [clojure.string :as str]
            [polylith.clj.core.command.message :as command]
            [polylith.clj.core.command.shared :as shared]
            [polylith.clj.core.common.interface :as common]
            [polylith.clj.core.git.interface :as git]))

(def ent->name {"w" "my-workspace"
                "p" "my-project"
                "b" "my-base"
                "c" "my-component"})

(defn workspace? [entity]
  (= "w" entity))

(defn base-or-comp? [entity]
  (contains? #{"b" "c"} entity))

(defn project-base-or-comp? [entity]
  (contains? #{"p" "b" "c"} entity))

(defn invalid-name? [name]
  (and name
       (str/includes? name ".")))

(defn git-repo? [{:keys [ws-dir]}]
  (git/is-git-repo? ws-dir))

(defn validate [workspace [cmd entity] name top-ns]
  (let [ent (shared/entity->short entity)]
    (if (= "create" cmd)
      (cond
        (nil? ent) [false "  The first argument after 'create' is expected to be any of: w, p, b, c, workspace, project, base, component."]
        (and (base-or-comp? ent)
             (invalid-name? name)) [false "  The . character is not allowed in brick names."]
        (and (common/toolsdeps1? workspace)
             (project-base-or-comp? ent)) [false "  Can't create bricks or projects in old workspaces. Execute the 'migrate' command to migrate the workspace. Execute the 'help migrate' command for instructions."]
        (and (nil? workspace)
             (project-base-or-comp? ent)) [false (command/cant-be-executed-outside-ws-message "create")]
        (and (nil? name)
             (or (not (workspace? ent))
                 (and (workspace? ent)
                      (-> workspace git-repo? not)))) [false (str "  A name must be given, e.g.: create " ent " name:" (ent->name ent))]
        (and (workspace? ent)
             (-> workspace nil? not)) [false "  A workspace should not be created within another workspace."]
        (and (workspace? ent)
             (str/blank? top-ns)) [false (str "  A top namespace must be given, e.g.: create " ent " name:" (ent->name ent) " top-ns:com.my-company")]
        :else [true])
      [true])))

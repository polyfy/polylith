(ns ^:no-doc polylith.clj.core.creator.interface
  (:require [polylith.clj.core.creator.base :as base]
            [polylith.clj.core.creator.component :as component]
            [polylith.clj.core.creator.workspace :as workspace]
            [polylith.clj.core.creator.project :as project]))

(defn create-workspace [workspace root-dir ws-name top-ns ws-dialects branch commit?]
  (workspace/create workspace root-dir ws-name top-ns ws-dialects branch commit?))

(defn create-project [workspace project-name dialect is-git-add]
  (project/create workspace project-name dialect is-git-add))

(defn create-component [workspace component-name interface-name dialect is-git-add]
  (component/create workspace component-name interface-name dialect is-git-add))

(defn create-base [workspace base-name dialect is-git-add]
  (base/create workspace base-name dialect is-git-add))

(defn print-alias-message [project-name project-alias color-mode]
  (project/print-alias-message project-name project-alias color-mode))

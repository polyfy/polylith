(ns polylith.clj.core.creator.interface
  (:require [polylith.clj.core.creator.base :as base]
            [polylith.clj.core.creator.component :as component]
            [polylith.clj.core.creator.workspace :as workspace]
            [polylith.clj.core.creator.project :as project]))

(defn create-workspace [root-dir ws-name top-ns branch git-add? commit?]
  (workspace/create root-dir ws-name top-ns branch git-add? commit?))

(defn create-project [workspace project-name is-git-add]
  (project/create workspace project-name is-git-add))

(defn create-component [workspace component-name interface-name is-git-add]
  (component/create workspace component-name interface-name is-git-add))

(defn create-base [workspace base-name is-git-add]
  (base/create workspace base-name is-git-add))

(defn print-alias-message [project-name color-mode]
  (project/print-alias-message project-name color-mode))

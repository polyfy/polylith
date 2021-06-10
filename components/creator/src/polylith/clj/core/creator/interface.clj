(ns polylith.clj.core.creator.interface
  (:require [polylith.clj.core.creator.base :as b]
            [polylith.clj.core.creator.component :as c]
            [polylith.clj.core.creator.workspace :as ws]
            [polylith.clj.core.creator.project :as project]))

(defn create-workspace [root-dir ws-name top-ns branch is-git-add]
  (ws/create root-dir ws-name top-ns branch is-git-add))

(defn create-project [workspace project-name is-git-add]
  (project/create workspace project-name is-git-add))

(defn create-component [workspace component-name interface-name is-git-add]
  (c/create workspace component-name interface-name is-git-add))

(defn create-base [workspace base-name is-git-add]
  (b/create workspace base-name is-git-add))

(defn print-alias-message [project-name color-mode]
  (project/print-alias-message project-name color-mode))

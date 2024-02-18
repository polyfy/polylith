(ns ^:no-doc polylith.clj.core.api.core
  (:require [clojure.string :as str]
            [polylith.clj.core.check.interface :as check]
            [polylith.clj.core.user-input.interface :as user-input]
            [polylith.clj.core.workspace.interface :as workspace]
            [polylith.clj.core.ws-explorer.interface :as ws-explorer]))

(defn projects-to-deploy [since]
  (let [since-then (str "since:" (or since "stable"))
        user-input (user-input/extract-arguments ["ws" since-then "skip:development"])
        workspace (workspace/workspace user-input)]
    (-> workspace :changes :changed-or-affected-projects)))

(defn key->str [key]
  (cond
    (number? key) (str key)
    (keyword? key) (name key)
    :else key))

(defn workspace [since keys]
  (let [keys-str (map key->str keys)
        since-str (str "since:" (key->str since))
        args (if (empty? keys-str)
               ["ws" since-str]
               ["ws" since-str (str "get:" (str/join ":" keys-str))])
        user-input (user-input/extract-arguments args)
        workspace (workspace/workspace user-input)]
    (if (empty? keys-str)
      workspace
      (ws-explorer/extract workspace keys-str))))

(defn check []
  (-> (workspace "stable" [])
      (check/check)))

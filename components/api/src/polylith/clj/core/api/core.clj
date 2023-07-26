(ns ^:no-doc polylith.clj.core.api.core
  (:require [clojure.string :as str]
            [polylith.clj.core.change.interface :as change]
            [polylith.clj.core.user-input.interface :as user-input]
            [polylith.clj.core.workspace.interface :as ws]
            [polylith.clj.core.workspace-clj.interface :as ws-clj]
            [polylith.clj.core.ws-explorer.interface :as ws-explorer]))

(defn projects-to-deploy
  [since]
  (let [since-then (str "since:" since)
        user-input (user-input/extract-params ["ws" since-then "skip:development"])
        workspace (-> user-input
                      ws-clj/workspace-from-disk
                      ws/enrich-workspace
                      change/with-changes)]
    (-> workspace :changes :changed-or-affected-projects)))

(defn key->str [key]
  (cond
    (number? key) (str key)
    (keyword? key) (name key)
    :else key))

(defn workspace
  "Returns the workspace or part of the workspace by sending in either
   a key that can be found in the :tag-patterns keys in workspace.edn,
   optionally prefixed with 'previous-', or a git SHA, as the first argument,
   and a list of keywords, strings, or numbers as the second argument.
   :keys and :count are also valid keys to send in. If keys are empty,
   returns the whole workspace."
  [since keys]
  (let [keys-str (map key->str keys)
        since-str (str "since:" (key->str since))
        args (if (empty? keys-str)
               ["ws" since-str]
               ["ws" since-str (str "get:" (str/join ":" keys-str))])
        user-input (user-input/extract-params args)
        workspace (-> user-input
                      ws-clj/workspace-from-disk
                      ws/enrich-workspace
                      change/with-changes)]
    (if (empty? keys-str)
      workspace
      (ws-explorer/extract workspace keys-str))))

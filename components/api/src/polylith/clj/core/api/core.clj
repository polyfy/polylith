(ns polylith.clj.core.api.core
  (:require [clojure.string :as str]
            [polylith.clj.core.change.interface :as change]
            [polylith.clj.core.user-input.interface :as user-input]
            [polylith.clj.core.workspace.interface :as ws]
            [polylith.clj.core.workspace-clj.interface :as ws-clj]
            [polylith.clj.core.ws-explorer.interface :as ws-explorer]))

(defn projects-to-deploy
  "Returns the projects that have been affected since last deploy,
   tagged in git following the pattern defined by :release-tag-pattern in
   deps.edn, or v[0-9]* if not defined."
  []
  (let [user-input (user-input/extract-params ["ws" "since:previous-release"])
        workspace (-> user-input
                      ws-clj/workspace-from-disk
                      ws/enrich-workspace
                      change/with-changes)]
    (filterv #(not= "development" %)
             (-> workspace :changes :changed-or-affected-projects))))

(defn key->str [key]
  (cond
    (number? key) (str key)
    (keyword? key) (name key)
    :else key))

(defn workspace
  "Returns the workspace or part of the workspace by sending in a stable-point
   that specifies which git tag to calculate changes and a list of keywords,
   strings, or numbers. :keys and :count are also valid keys to send in. If keys
   are empty, returns the whole workspace."
  [stable-point keys]
  (let [keys-str (map key->str keys)
        since (if (= :previous-release stable-point)
                "since:previous-release" "since:stable")
        args (if-not (empty keys-str)
               ["ws" since (str "get:" (str/join ":" keys-str))]
               ["ws" since])
        user-input (user-input/extract-params args)
        workspace (-> user-input
                      ws-clj/workspace-from-disk
                      ws/enrich-workspace
                      change/with-changes)]
    (if (empty? keys-str)
      workspace
      (ws-explorer/extract workspace keys-str))))

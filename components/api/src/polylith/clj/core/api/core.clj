(ns polylith.clj.core.api.core
  (:require [clojure.string :as str]
            [polylith.clj.core.change.interface :as change]
            [polylith.clj.core.user-input.interface :as user-input]
            [polylith.clj.core.workspace.interface :as ws]
            [polylith.clj.core.workspace-clj.interface :as ws-clj]
            [polylith.clj.core.ws-explorer.interface :as ws-explorer]))

(defn environments-to-deploy []
  "Returns the environments that have been affected since last deploy,
   tagged in git following the pattern defined by :build-tag-pattern in
   deps.edn, or v* if not defined."
  (let [user-input (user-input/extract-params [])
        workspace (-> user-input
                      ws-clj/workspace-from-disk
                      ws/enrich-workspace
                      change/with-previous-build-changes)]
    (filterv #(not= "development" %)
             (-> workspace :changes :changed-or-affected-environments))))

(defn key->str [key]
  (cond
    (number? key) (str key)
    (keyword? key) (name key)
    :else key))

(defn workspace [stable-point keys]
  "Returns the workspace or part of the workspace by sending in a stable-point
   that specifies the which git tag to calculate changes and a list of keywords,
   strings, or numbers. :keys and :count are also valid keys to send in. If keys
   are empty, returns the whole workspace."
  (let [keys-str (map key->str keys)
        args (if-not (empty keys-str)
               ["ws" (str "get:" (str/join ":" keys-str))]
               ["ws"])
        user-input (user-input/extract-params args)
        stable-fn (if (= :build stable-point)
                    change/with-previous-build-changes
                    change/with-last-stable-changes)
        workspace (-> user-input
                      ws-clj/workspace-from-disk
                      ws/enrich-workspace
                      stable-fn)]
    (if (empty? keys-str)
      workspace
      (ws-explorer/extract workspace keys-str))))

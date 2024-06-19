(ns ^:no-doc polylith.clj.core.shell.candidate.selector.ws-shortcuts
  (:require [polylith.clj.core.shell.candidate.creators :as c]
            [polylith.clj.core.user-config.interface :as user-config])
  (:refer-clojure :exclude [alias]))

(defn description [{:keys [file dir]}]
  {:description (or file dir)})

(defn select [candidate _ _]
  (let [group-id (-> candidate :group :id)]
    (map #(c/single-txt (:name %) (c/group group-id) (description %))
         (user-config/ws-shortcuts-paths))))

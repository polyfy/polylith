(ns ^:no-doc polylith.clj.core.shell.candidate.selector.ws-shortcuts
  (:require [polylith.clj.core.shell.candidate.creators :as c]
            [polylith.clj.core.user-config.interface :as user-config])
  (:refer-clojure :exclude [alias]))

(defn description [{:keys [file dir]}]
  {:description (or file dir)})

(defn select-dirs [candidate _ _]
  (let [group-id (-> candidate :group :id)]
    (map #(c/single-txt (:name %) (c/group group-id) (description %))
         (filter :dir (user-config/ws-configs)))))

(defn select-files [candidate _ _]
  (let [group-id (-> candidate :group :id)]
    (map #(c/single-txt (:name %) (c/group group-id) (description %))
         (filter :file (user-config/ws-configs)))))

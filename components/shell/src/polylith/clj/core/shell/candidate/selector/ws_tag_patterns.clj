(ns polylith.clj.core.shell.candidate.selector.ws-tag-patterns
  (:require [polylith.clj.core.shell.candidate.creators :as c]))

(defn tag-keys [tag-pattern-key]
  (let [tag-name (name tag-pattern-key)]
    [tag-name (str "previous-" tag-name)]))

(defn select [candidate _ {:keys [settings]}]
  (let [group-id (-> candidate :group :id)]
    (map #(c/single-txt % (c/group group-id))
         (mapcat tag-keys
                 (-> settings :tag-patterns keys)))))

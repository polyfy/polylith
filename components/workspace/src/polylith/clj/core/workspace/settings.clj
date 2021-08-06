(ns polylith.clj.core.workspace.settings
  (:require [clojure.set :as set]))

(defn src-test-name [{:keys [name]} src-name->short-name]
  [name
   (src-name->short-name name)])

(defn undefined-project [index project-name]
  [project-name {:alias (str "?" (inc index))}])

(defn enrich-settings [settings projects]
  "Enrich project aliases with e.g.: '?1', '?2'"
  (let [conf-projects (merge {"development" {:alias "dev"}} (:projects settings))
        undefined-projects (set/difference (set (map :name projects))
                                           (set (map first (filter #(-> % second :alias)
                                                                   conf-projects))))
        enriched-projects (merge (into {} (map-indexed undefined-project undefined-projects))
                                 conf-projects)]
    (assoc settings :projects enriched-projects)))

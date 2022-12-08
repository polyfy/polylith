(ns polylith.clj.core.workspace.settings.alias
  (:require [clojure.set :as set]))

(defn project-name->alias [index project-name]
  [project-name (str "?" (inc index))])

(defn inject-alias-reducer [acc [project-name alias]]
  (update acc project-name assoc :alias alias))

(defn inject-dev-alias-if-missing [project-settings]
  (if (get-in project-settings ["development" :alias])
    project-settings
    (update project-settings "development" assoc :alias "dev")))

(defn enrich-settings [settings projects]
  "Enrich project aliases with e.g.: 'dev', '?1', '?2'"
  (let [project-settings (:projects settings)
        project-names (->> projects
                           (map :name)
                           (set))
        project-names-without-alias (->> project-settings
                                         (filter #(-> % second :alias))
                                         (map first)
                                         (set))
        missing-project-names (set/difference project-names project-names-without-alias)
        enriched-projects (->> missing-project-names
                               (map-indexed project-name->alias)
                               (reduce inject-alias-reducer project-settings)
                               (inject-dev-alias-if-missing))]
    (assoc settings :projects enriched-projects)))

(ns polylith.clj.core.workspace.settings
  (:require [clojure.set :as set]))

(defn src-test-name [{:keys [name]} src-name->short-name]
  [name
   (src-name->short-name name)])

(defn undefined-project [index project-name]
  [project-name {:alias (str "?" (inc index))}])

(def default-test-runner
  'polylith.clj.core.clojure-test-test-runner.interface/create)

(defn add-default-test-runner-if-missing [acc [project-name {:keys [test] :as project}]]
  (let [enriched-project (if (or (empty? test)
                                 (empty? (:create-test-runner test)))
                           (assoc project :test {:create-test-runner [default-test-runner]})
                           project)]
    (assoc acc project-name enriched-project)))

(defn enrich-settings [settings projects]
  "Enrich project aliases with e.g.: '?1', '?2'"
  (let [conf-projects (merge {"development" {:alias "dev"}} (:projects settings))
        undefined-projects (set/difference (set (map :name projects))
                                           (set (map first (filter #(-> % second :alias)
                                                                   conf-projects))))
        enriched-projects (reduce add-default-test-runner-if-missing
                                  {}
                                  (merge (into {} (map-indexed undefined-project undefined-projects))
                                         conf-projects))]
    (assoc settings :projects enriched-projects)))

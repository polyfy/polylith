(ns polylith.clj.core.workspace.alias
  (:require [clojure.set :as set]))

(defn src-test-name [{:keys [name]} src-name->short-name]
  [name
   (src-name->short-name name)])

(defn undefined-project [index project-name]
  [project-name (str "?" (inc index))])

(defn abbreviated-project-names [project-to-alias src-names]
  (let [undefined-projects (set/difference (set src-names)
                                           (set (keys project-to-alias)))]
    (if (empty? undefined-projects)
      project-to-alias
      (merge project-to-alias
             (into {} (map-indexed undefined-project undefined-projects))))))

(defn project-to-alias [{:keys [project-to-alias]} projects]
  (let [src-names (mapv :name projects)
        proj-to-allias (if project-to-alias
                         (if (contains? project-to-alias "development")
                           project-to-alias
                           (conj project-to-alias ["development" "dev"]))
                         {"development" "dev"})]
    (abbreviated-project-names proj-to-allias src-names)))

(ns polylith.clj.core.validator.m104-circular-deps
  (:require [clojure.string :as str]
            [polylith.clj.core.util.interface :as util]
            [polylith.clj.core.util.interface.color :as color]))

(defn dep [[_ {:keys [src test]}] project-name]
  (let [circular (or (:circular src)
                     (:circular test))]
    {:project-name  project-name
     :circular-deps circular}))

(defn circular-dep [{:keys [name deps]}]
  (util/find-first :circular-deps
                   (concat (map #(dep % name) deps))))

(defn projects-circular-deps [{:keys [project-name circular-deps]} color-mode]
  (let [deps (str/join " > " circular-deps)
        message (str "A circular dependency was found in the "
                     (color/project project-name color-mode) " project: "
                     (color/component deps color-mode))]
    (when (-> circular-deps empty? not)
      [(util/ordered-map :type "error"
                         :code 104
                         :message (color/clean-colors message)
                         :colorized-message message
                         :components (vec (sort (set circular-deps)))
                         :project project-name)])))

(defn errors [projects color-mode]
  (util/first-as-vector (mapcat #(projects-circular-deps % color-mode)
                                (filter identity (map circular-dep projects)))))

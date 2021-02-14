(ns polylith.clj.core.validator.m109-missing-libraries
  (:require [clojure.set :as set]
            [clojure.string :as str]
            [polylith.clj.core.validator.shared :as shared]
            [polylith.clj.core.util.interface :as util]
            [polylith.clj.core.util.interface.color :as color]))

(defn brick-lib-deps [{:keys [lib-deps]}]
  (vec (keys lib-deps)))

(defn warning [project-name missing-libraries color-mode]
  (let [libs (str/join ", " (sort missing-libraries))
        message (str "Missing libraries in the " (color/project project-name color-mode) " project: " (color/grey color-mode libs))]
    [(util/ordered-map :type "error"
                       :code 109
                       :message (color/clean-colors message)
                       :colorized-message message
                       :project project-name)]))

(defn project-warning [{:keys [name component-names base-names lib-deps profile]} bricks used-libs color-mode]
  (let [existing-libs (set (map first (concat lib-deps (:lib-deps profile))))
        brick-names (concat component-names base-names)
        brick->lib-dep-names (into {} (map (juxt :name brick-lib-deps) bricks))
        expected-libs (set (mapcat brick->lib-dep-names brick-names))
        missing-libs (set/intersection used-libs (set/difference expected-libs existing-libs))]
    (if (-> missing-libs empty? not)
      (warning name missing-libs color-mode))))

(defn errors [cmd {:keys [input-type profile-to-settings active-profiles] :as settings} projects components bases color-mode]
  (when (and (= :toolsdeps1 input-type)
             (shared/show-error? cmd profile-to-settings active-profiles))
    (let [bricks (concat components bases)
          used-libs (shared/used-libs projects settings)]
      (mapcat #(project-warning % bricks used-libs color-mode)
              projects))))

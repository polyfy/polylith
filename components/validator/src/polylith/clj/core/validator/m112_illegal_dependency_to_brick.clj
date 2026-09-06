(ns ^:no-doc polylith.clj.core.validator.m112-illegal-dependency-to-brick
  (:require [clojure.string :as str]
            [polylith.clj.core.util.interface :as util]
            [polylith.clj.core.util.interface.color :as color]
            [polylith.clj.core.util.interface.str :as str-util]))

(defn brick-path? [path]
  (and path
       (or (str/starts-with? path "../../bases/")
           (str/starts-with? path "../../components/"))))

(defn illegal-brick-path [{:keys [paths]}]
  (filterv brick-path? paths))

(defn illegal-brick-dep [[_ {:keys [local/root]}]]
  (if (brick-path? root)
    [root]
    []))

(defn brick-errors [{:keys [name type deps]} color-mode]
  (let [illegal-dep-paths (mapcat illegal-brick-dep (:deps deps))
        illegal-paths (illegal-brick-path deps)
        paths (concat illegal-dep-paths illegal-paths)]
    (when (seq paths)
      (let [brick (str-util/maybe-in-plural "brick" paths)
            dependency (str-util/maybe-in-plural "dependency" "dependencies" paths)
            message (str "Illegal " dependency " on " brick " from the '"
                         (color/brick type name color-mode) "' " type " (in deps.edn): "
                         (str/join ", " paths))]
        (util/ordered-map :type "error"
                          :code 112
                          :message (color/clean-colors message)
                          :colorized-message message
                          :brick name)))))

(defn errors [{:keys [bases components]} color-mode]
  (filterv identity
           (map #(brick-errors % color-mode)
                (concat bases components))))

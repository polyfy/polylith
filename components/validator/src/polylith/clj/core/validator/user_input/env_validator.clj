(ns polylith.clj.core.validator.user-input.env-validator
  (:require [clojure.set :as set]
            [clojure.string :as str]
            [polylith.clj.core.util.interface.color :as color]))

(defn validate [selected-environments environments color-mode]
  (let [missing (set/difference selected-environments
                                (set (mapcat (juxt :alias :name) environments)))
        s (if (= 1 (count missing)) "" "s")
        missing-msg (color/environment (str/join ", " missing) color-mode)]
    (when (-> missing empty? not)
      [(str "  Can't find environment" s ": " missing-msg)])))

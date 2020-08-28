(ns polylith.clj.core.validator.m206-reference-to-missing-library-in-ns-lib
  (:require [clojure.set :as set]
            [clojure.string :as str]
            [polylith.clj.core.util.interfc :as util]
            [polylith.clj.core.util.interfc.color :as color]))

(defn libs [{:keys [lib-deps]}]
  (map first lib-deps))

(defn warnings [settings environments color-mode]
  (let [mapped-libs (set (map second (:ns->lib settings)))
        used-libs (set (mapcat libs environments))
        missing-libs (set/difference mapped-libs used-libs)
        message (str "Reference to missing library was found in the :ns->lib mapping: "
                     (color/library (str/join ", " missing-libs) color-mode))]
    (when (-> missing-libs empty? not)
      [(util/ordered-map :type "warning"
                         :code 206
                         :message (color/clean-colors message)
                         :colorized-message message)])))

(ns polylith.clj.core.validator.m205-reference-to-missing-library-in-ns-lib
  (:require [clojure.set :as set]
            [clojure.string :as str]
            [polylith.clj.core.validator.shared :as shared]
            [polylith.clj.core.util.interface :as util]
            [polylith.clj.core.util.interface.color :as color]))

(defn warnings [settings projects color-mode]
  (let [mapped-libs (set (map second (:ns-to-lib settings)))
        used-libs (shared/used-libs projects settings)
        missing-libs (set/difference mapped-libs used-libs)
        message (str "Reference to missing library was found in the :ns-to-lib mapping: "
                     (color/library (str/join ", " missing-libs) color-mode))]
    (when (-> missing-libs empty? not)
      [(util/ordered-map :type "warning"
                         :code 205
                         :message (color/clean-colors message)
                         :colorized-message message)])))

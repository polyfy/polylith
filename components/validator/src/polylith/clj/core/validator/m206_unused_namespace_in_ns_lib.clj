(ns polylith.clj.core.validator.m206-unused-namespace-in-ns-lib
  (:require [clojure.set :as set]
            [polylith.clj.core.util.interfc :as util]
            [polylith.clj.core.util.interfc.color :as color]
            [clojure.string :as str]))

(defn warnings [settings components bases color-mode]
  (let [bricks (concat components bases)
        mapped-libs (set (map second (:ns->lib settings)))
        used-libs (set (mapcat :lib-dep-names bricks))
        missing-libs (set/difference mapped-libs used-libs)
        message (str "Undefined libraries was found in :ns->lib settings: "
                     (color/library (str/join ", " missing-libs) color-mode))]
    (when (-> missing-libs empty? not)
      [(util/ordered-map :type "warning"
                         :code 206
                         :message (color/clean-colors message)
                         :colorized-message message)])))

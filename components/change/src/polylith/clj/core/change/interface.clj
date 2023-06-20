(ns polylith.clj.core.change.interface
  (:require [polylith.clj.core.change.core :as core]
            [polylith.clj.core.util.interface.time :as time-util]))

(defn with-changes [workspace]
  (time-util/tap-seconds "#with-changes" (core/with-changes workspace)))

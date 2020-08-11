(ns polylith.clj.core.change.interfc
  (:require [polylith.clj.core.change.core :as core]))

(defn with-changes
  ([workspace user-input]
   (core/with-changes workspace user-input))
  ([workspace changed-files user-input]
   (core/with-changes workspace changed-files user-input)))

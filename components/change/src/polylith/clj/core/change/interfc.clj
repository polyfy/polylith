(ns polylith.clj.core.change.interfc
  (:require [polylith.clj.core.change.core :as core]))

(defn with-changes
  ([workspace test-settings]
   (core/with-changes workspace test-settings))
  ([workspace changed-files test-settings]
   (core/with-changes workspace changed-files test-settings)))

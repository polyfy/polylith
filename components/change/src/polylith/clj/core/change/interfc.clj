(ns polylith.clj.core.change.interfc
  (:require [polylith.clj.core.change.core :as core]))

(defn with-changes
  ([workspace enable-dev?]
   (core/with-changes workspace enable-dev?))
  ([workspace changed-files enable-dev?]
   (core/with-changes workspace changed-files enable-dev?)))

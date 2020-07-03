(ns polylith.core.change.interface
  (:require [polylith.core.change.core :as core]))

(defn changed-files [{:keys [environments]} sha1 sha2]
  (core/changed-files environments sha1 sha2))

(defn with-changes
  ([workspace]
   (with-changes workspace (changed-files workspace nil nil)))
  ([workspace changed-files]
   (core/with-changes workspace changed-files)))

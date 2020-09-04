(ns polylith.clj.core.workspace-clj.interface
  (:require [polylith.clj.core.workspace-clj.core :as core]))

(defn workspace-from-disk
  ([user-input]
   (core/workspace-from-disk user-input)))

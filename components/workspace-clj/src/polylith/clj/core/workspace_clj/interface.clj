(ns polylith.clj.core.workspace-clj.interface
  (:require [polylith.clj.core.workspace-clj.core :as core]))

(defn workspace-from-disk [user-input]
  "Reads the workspace from disk, or from a file if 'ws-file'
   is given in the user-input, and stores it in a hash map."
  (core/workspace-from-disk user-input))

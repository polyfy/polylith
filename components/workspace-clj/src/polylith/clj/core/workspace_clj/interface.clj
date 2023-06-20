(ns polylith.clj.core.workspace-clj.interface
  (:require [polylith.clj.core.workspace-clj.core :as core]
            [polylith.clj.core.util.interface.time :as time-util]))

(defn workspace-from-disk
  "Reads the workspace from disk, or from a file if 'ws-file'
   is given in the user-input, and stores it in a hash map."
  [user-input]
  (time-util/tap-seconds "#workspace-from-disk" (core/workspace-from-disk user-input)))

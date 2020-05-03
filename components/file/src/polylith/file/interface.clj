(ns polylith.file.interface
  (:require [polylith.file.core :as core]))

(defn delete-folder [file]
  (core/delete-folder file))

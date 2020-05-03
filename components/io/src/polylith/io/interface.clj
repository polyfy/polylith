(ns polylith.io.interface
  (:require polylith.io.file :as f))

(defn delete-folder [file]
  (f/delete-folder file))

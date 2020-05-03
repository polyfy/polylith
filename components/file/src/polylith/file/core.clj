(ns polylith.file.core
  (:require [clojure.java.io :as io])
  (:import (java.io File)))

(defn delete-folder [file]
  (let [files (reverse (file-seq file))]
    (doseq [^File f files]
      (when (.exists f)
        (io/delete-file f)))))

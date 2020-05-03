(ns polylith.io.file)

(defn delete-folder [file]
  (let [files (reverse (file-seq file))]
    (doseq [^File f files]
      (when (.exists f)
        (io/delete-file f)))))

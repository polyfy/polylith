(ns polylith.clj.core.file.core
  (:require [clojure.java.io :as io]
            [clojure.java.io :refer [reader]])
  (:import (java.io File FileNotFoundException PushbackReader)))

(defn exists [path]
  (.exists (File. path)))

(defn absolute-path [path]
  (-> path io/file .getAbsolutePath))

(defn directory? [^File file]
  (.isDirectory file))

(defn file-name [^File file]
  (.getName file))

(defn lines-of-code [file-path]
  (with-open [rdr (reader file-path)]
    (count (line-seq rdr))))

(defn directory-paths [dir]
  (->> dir
       (io/file)
       (.listFiles)
       (filter directory?)
       (mapv file-name)
       (into #{})))

(defn read-file [path]
  (try
    (with-open [rdr (-> path
                        (io/reader)
                        (PushbackReader.))]
      (doall
        (take-while #(not= ::done %)
                    (repeatedly #(try (read rdr)
                                      (catch Exception _ ::done))))))
    (catch FileNotFoundException _
      nil)))

(defn delete-folder [file]
  (let [files (reverse (file-seq file))]
    (doseq [^File f files]
      (when (.exists f)
        (io/delete-file f)))))

(defn files-recursively [dir-path]
  (drop-last (reverse (file-seq (io/file dir-path)))))

(defn paths-recursively [dir]
  (map str (files-recursively dir)))

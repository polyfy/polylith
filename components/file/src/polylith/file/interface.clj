(ns polylith.file.interface
  (:require [polylith.file.core :as core]
            [clojure.java.io :as io])
  (:import (java.io File)))

(defn directory? [^File file]
  (core/directory? file))

(defn file-name [^File file]
  (core/file-name file))

(defn directory-names [dir]
  (core/directory-names dir))

(defn read-file [path]
  (core/read-file path))

(defn delete-folder [file]
  (core/delete-folder file))

(defn files-recursively [dir-path]
  (core/paths dir-path))

(defn all-paths [dir-path]
  (core/all-paths dir-path))

(ns polylith.file.interface
  (:require [polylith.file.core :as core])
  (:import (java.io File)))

(defn directory? [^File file]
  (core/directory? file))

(defn file-name [^File file]
  (core/file-name file))

(defn directory-paths [dir]
  (core/directory-paths dir))

(defn read-file [path]
  (core/read-file path))

(defn delete-folder [file]
  (core/delete-folder file))

(defn files-recursively [dir-path]
  (core/files-recursively dir-path))

(defn number-of-lines [path]
  (core/number-of-lines path))

(defn paths-recursively [dir]
  "Returns all directories and files in a directory recursively"
  (core/paths-recursively dir))

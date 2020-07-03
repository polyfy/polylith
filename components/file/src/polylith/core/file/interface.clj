(ns polylith.core.file.interface
  (:require [polylith.core.file.core :as core])
  (:import (java.io File)))

(defn absolute-path [path]
  (core/absolute-path path))

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

(defn lines-of-code [file-path]
  (core/lines-of-code file-path))

(defn paths-recursively [dir]
  "Returns all directories and files in a directory recursively"
  (core/paths-recursively dir))

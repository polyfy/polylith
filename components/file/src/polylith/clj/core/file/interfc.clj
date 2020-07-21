(ns polylith.clj.core.file.interfc
  (:require [polylith.clj.core.file.core :as core])
  (:import (java.io File)))

(defn absolute-path [path]
  (core/absolute-path path))

(defn create-dir [^String path]
  (core/create-dir path))

(defn create-file [path rows]
  (core/create-file path rows))

(defn create-temp-dir [dir]
  (core/create-temp-dir dir))

(defn copy-resource-file! [source target-path]
  (core/copy-resource-file! source target-path))

(defn current-path []
  (core/current-path))

(defn delete-dir [path]
  (core/delete-dir path))

(defn delete-file [path]
  (core/delete-file path))

(defn directory? [^File file]
  (core/directory? file))

(defn exists [path]
  (core/exists path))

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

(defn relative-paths [path]
  (core/relative-paths path))

(ns polylith.clj.core.file.interface
  (:require [me.raynes.fs :as fs]
            [polylith.clj.core.file.core :as core])
  (:import (java.io File)))

(def sep (File/separator))

(defn absolute-path [path]
  (core/absolute-path path))

(defn create-dir [^String path]
  (core/create-dir path))

(defn create-file [path rows]
  (core/create-file path rows))

(defn create-missing-dirs [filename]
  (core/create-missing-dirs filename))

(defn create-temp-dir [dir]
  (core/create-temp-dir dir))

(defn copy-file-or-dir+ [from to]
  (core/copy-file-or-dir+ from to))

(defn copy-file [from to]
  (fs/copy from to))

(defn copy-file+ [from to]
  (fs/copy+ from to))

(defn copy-dir [from to]
  (fs/copy-dir from to))

(defn copy-resource-file! [source target-path]
  (core/copy-resource-file! source target-path))

(defn current-dir []
  (core/current-dir))

(defn delete-dir [path]
  (core/delete-dir path))

(defn delete-file [path]
  (core/delete-file path))

(defn directory? [^File file]
  (core/directory? file))

(defn exists [path]
  (core/exists path))

(defn file-name [^File file]
  (core/filename file))

(defn directories [dir]
  (core/directory-paths dir))

(defn read-file [path]
  (core/read-file path))

(defn size [path]
  (fs/size path))

(defn files-recursively [dir]
  (core/files-recursively dir))

(defn files [dir]
  (core/files dir))

(defn lines-of-code [file-path]
  (core/lines-of-code file-path))

(defn paths-recursively
  "Returns all directories and files in a directory recursively"
  [dir]
  (core/paths-recursively dir))

(defn relative-paths [path]
  (core/relative-paths path))

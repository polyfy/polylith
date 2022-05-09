(ns polylith.clj.core.file.interface
  (:require [me.raynes.fs :as fs]
            [clojure.string :as cstr]
            [polylith.clj.core.file.core :as core])
  (:import (java.io File)))

(def sep (File/separator))

(defn file [^String f]
  (core/file f))

(defn absolute-path [path]
  (core/absolute-path path))

(defn create-dir [^String path]
  (core/create-dir path))

(defn create-file [path rows]
  (core/create-file path rows true))

(defn create-file-if-not-exists [path rows]
  (core/create-file path rows false))

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

(defn files-and-dirs [dir home-dir]
  (core/dirs-and-files dir home-dir))

(defn read-deps-file [path]
  (core/read-deps-file path))

(defn read-file [path]
  (core/read-file path))

(defn size [path]
  (core/size path))

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

(defn pretty-spit [filename collection]
  (core/pretty-spit filename collection))


;; (defn make-deps-path [deps-segment component-dir top-src-dir component-name interface-ns]
;;   (let [deps-path (cstr/join "/" [component-dir deps-segment top-src-dir component-name (str interface-ns ".clj")])]
;;     deps-path))


;; (defn check-deps-path [deps-segment component-dir top-src-dir component-name interface-ns]
;;   (let [deps-path (make-deps-path deps-segment component-dir top-src-dir component-name interface-ns)]
;;     (if (.exists (java.io.File. deps-path))
;;       deps-path)))


;; (defn find-deps-path [deps-segments component-dir top-src-dir component-name interface-ns]
;;   (let [deps-paths (map #(check-deps-path % component-dir top-src-dir component-name interface-ns) deps-segments)]
;;     (if (= (count deps-paths) 1)
;;       (first deps-paths))))

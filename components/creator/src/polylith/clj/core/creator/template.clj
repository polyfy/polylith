(ns ^:no-doc polylith.clj.core.creator.template
  (:require [clojure.java.io :as io]
            [selmer.parser :as parser]))

(defn- default-template [template]
  (io/resource (str "creator/templates/" template)))

(defn- file-if-exists [path]
  (let [file (io/file path)]
    (when (.exists file)
      file)))

(defn- user-template [template]
  (file-if-exists (str (System/getProperty "user.home") "/.config/polylith/templates/" template)))

(defn- project-template [ws-dir template]
  (file-if-exists (str ws-dir "/.polylith/templates/" template)))

(defn- find-template [ws-dir template]
  (or (project-template ws-dir template)
      (user-template template)
      (default-template template)))

(defn render [ws-dir template data]
  (when-let [readable-template (find-template ws-dir template)]
    (parser/render (slurp readable-template) data)))

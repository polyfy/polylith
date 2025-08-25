(ns ^:no-doc polylith.clj.core.creator.template
  (:require [clojure.java.io :as io]
            [selmer.parser :as parser]))

(defn- default-template [template dialect]
  (if (= "cljs" dialect)
    (or (io/resource (str "creator/templates/" (str template "s")))
        (io/resource (str "creator/templates/" template)))
    (io/resource (str "creator/templates/" template))))

(defn- file-if-exists
  ([path]
   (let [file (io/file path)]
     (when (.exists file)
       file)))
   ([path dialect]
    (if (= "cljs" dialect)
      (or (file-if-exists (str path "s"))
          (file-if-exists path))
      (file-if-exists path))))

(defn- user-template [template dialect]
  (file-if-exists (str (System/getProperty "user.home") "/.config/polylith/templates/" template) dialect))

(defn- project-template [ws-dir template dialect]
  (file-if-exists (str ws-dir "/.polylith/templates/" template) dialect))

(defn- find-template [ws-dir template dialect]
  (or (project-template ws-dir template dialect)
      (user-template template dialect)
      (default-template template dialect)))

(defn render [ws-dir template {:keys [dialect] :as data}]
  (when-let [readable-template (find-template ws-dir template dialect)]
    (parser/render (slurp readable-template) data)))

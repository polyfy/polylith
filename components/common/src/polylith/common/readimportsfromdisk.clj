(ns polylith.common.readimportsfromdisk
  (:require [polylith.file.interface :as file]
            [clojure.string :as str]))

(defn require? [val]
  (and (sequential? val)
       (= :require (first val))))

(defn ->imports [imports]
  (rest (first (filter require? imports))))

(defn filter-imports [content]
  (mapv first (filterv #(= :as (second %))
                       (->imports (first content)))))

(defn short-path [root-dir path]
  (subs path (count root-dir)))

(defn imports [root-dir file-path]
  (let [content (file/read-file file-path)]
    {:path    (short-path root-dir file-path)
     :imports (filter-imports content)}))

(defn all-imports [root-dir]
  (let [file-paths (filter #(str/ends-with? % ".clj")
                           (file/all-paths root-dir))]
    (map #(imports root-dir %) file-paths)))

;(all-imports "../clojure-polylith-realworld-example-app/components/user/src")
;(all-imports "../Nova/project-unicorn/components/talent/")



; clojure.realworld.user.core

;(def all-paths (file/all-paths "../clojure-polylith-realworld-example-app/components/user/src"))
;
;
;(def dir "../clojure-polylith-realworld-example-app/components/user/src")
;(def path (first all-paths))
;
;(file/read-file (first all-paths))
;
;
;
;
;
;(def clojure-files (filter #(str/ends-with? % ".clj") all-paths))
;
;(def content (file/read-file (first clojure-files)))
;
;
;
;(imports "../clojure-polylith-realworld-example-app/components/user/src")
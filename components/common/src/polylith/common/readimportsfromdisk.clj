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
                           (file/paths-recursively root-dir))]
    (mapv #(imports root-dir %) file-paths)))

;(all-imports "../clojure-polylith-realworld-example-app/components/user/src")
;(all-imports "../clojure-polylith-realworld-example-app/components/user/src/clojure/realworld/")
;(all-imports "../Nova/project-unicorn/components/talent/")

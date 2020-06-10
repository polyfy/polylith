(ns polylith.workspace-clj.componentsfromdisk
  (:require [polylith.file.interface :as file]
            [polylith.workspace-clj.importsfromdisk :as importsfromdisk]
            [clojure.string :as str]))

(def ->generic-type {'def "data"
                     'defn "function"
                     'defmacro "macro"})

(defn definition? [code]
  (if (list? code)
    (let [f (first code)]
      (or (= f 'def)
          (= f 'defn)
          (= f 'defmacro)))
    false))

(defn filter-statements [statements]
  (filterv definition?
           ; Drops the namespace declaration on top of the file
           (drop 1 statements)))

(defn ->function [type name code]
  {:name (str name)
   :type (str type)
   :parameters (mapv str (first code))})

(defn ->definitions [statement]
  "Takes a statement (def, defn or defmacro) from source code
   and returns a vector of data function definitions."
  (let [type (-> statement first ->generic-type)
        name (second statement)
        code (drop-while #(not (or (list? %)
                                   (vector? %)))
                         statement)]
    (if (= "data" type)
      [{:name (str name)
        :type (str type)}]
      (if (-> code first vector?)
        [(->function type name code)]
        (mapv #(->function type name %) code)))))

(defn replace-underscore [string]
  (str/replace string "_" "-"))

(defn read-component-from-disk [ws-path top-src-dir component-name]
  (let [component-src-dir (str ws-path "/components/" component-name "/src/" top-src-dir)
        ; Only one folder should be in each components base src folder.
        ; The name of the folder will be the name of the interface,
        ; in case the component's name is not same as it's interface.
        interface-name (-> component-src-dir file/directory-paths first replace-underscore)
        src-dir (str component-src-dir interface-name)
        interface-file-content (file/read-file (str src-dir "/interface.clj"))
        imports (importsfromdisk/all-imports component-src-dir)
        statements (filter-statements interface-file-content)
        definitions (vec (sort-by (juxt :name :type :parameters)
                                  (mapcat ->definitions statements)))]
    {:name component-name
     :type "component"
     :imports imports
     :interface {:name interface-name
                 :definitions definitions}}))

(defn read-components-from-disk [ws-path top-src-dir component-names]
  (vec (sort-by :name (map #(read-component-from-disk ws-path top-src-dir %) component-names))))

;(def ws-path "../clojure-polylith-realworld-example-app")
;(def component-src-dir (str ws-path "/components/article/src/clojure/realworld/"))
;(def interface-name (-> component-src-dir file/directory-paths first replace-underscore))
;(def src-dir (str component-src-dir interface-name))
;(def interface-file-content (file/read-file (str src-dir "/interface.clj")))
;(def imports (importsfromdisk/all-imports component-src-dir))

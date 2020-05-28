(ns polylith.srcreader-clj.readcomponentsfrom-disk
  (:require [polylith.file.interface :as file]
            [polylith.srcreader-clj.readimportsfromdisk :as importsfromdisk]))

(def type->generic-type {'defn 'function
                         'defmacro 'macro})

(defn definition? [code]
  (if (list? code)
    (let [f (first code)]
      (or (= f 'def)
          (= f 'defn)
          (= f 'defmacro)))
    false))

(defn filter-declarations [statements]
  (filterv definition?
           ; Drops the namespace declaration on top of the file
           (drop 1 statements)))

(defn ->declarations-info [statement]
  "Takes a statement (def, defn or defmacro) from source code,
   and returns a vector of information about those statements."
  (let [type (first statement)
        name (second statement)
        code (drop-while #(not (or (list? %)
                                   (vector? %)))
                         statement)]
    (if (= 'def type)
      {:type 'data
       :name name}
      {:type (type->generic-type type)
       :name name
       :overloads (if (vector? (first code))
                    (vector {:args  (first code)
                             :arity (-> code first count)})
                    (vec (sort-by :arity (map #(hash-map :args (first %)
                                                         :arity (-> % first count))
                                              code))))})))

(defn read-component-from-disk [ws-path top-src-dir component-name]
  (let [component-src-dir (str ws-path "/components/" component-name "/src/" top-src-dir)
        ; Only one folder should be in each components base src folder.
        ; The name of the folder will be the name of the interface,
        ; in case the component's name is not same as it's interface.
        interface-name (first (file/directory-paths component-src-dir))
        src-dir (str component-src-dir interface-name)
        interface-file-content (file/read-file (str src-dir "/interface.clj"))
        imports (importsfromdisk/all-imports component-src-dir)
        declarations (filter-declarations interface-file-content)
        declarations-infos (vec (sort-by (juxt :type :name) (map ->declarations-info declarations)))]
    {:type :component
     :name component-name
     :imports imports
     :interface {:name interface-name
                 :declarations declarations-infos}}))
     ;:dependencies (deps/dependencies top-ns component-name component-names imports)}))

(defn read-components-from-disk [ws-path top-src-dir component-names]
  (set (mapv #(read-component-from-disk ws-path top-src-dir %) component-names)))

;(read-component-from-disk "." "polylith/" "common")

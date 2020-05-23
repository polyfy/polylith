(ns polylith.common.readbricksfromdisk
  (:require [clojure.string :as str]
            [polylith.file.interface :as file]
            [polylith.common.readimportsfromdisk :as importsfromdisk]))

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
      {:type type
       :name name}
      {:type type
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
    {:type      :component
     :name      component-name
     :imports   imports
     :interface {:name         interface-name
                 :declarations declarations-infos}}))

;(all-bricks "../clojure-polylith-realworld-example-app")
;(all-bricks "../Nova/project-unicorn")



;(read-component-from-disk "../clojure-polylith-realworld-example-app" "clojure/realworld/" "article")
;(read-component-from-disk "../Nova/project-unicorn" "" "talent")


(defn read-base-from-disk [ws-path top-src-dir base-name]
  (let [bases-src-dir (str ws-path "/bases/" base-name "/src/" top-src-dir)
        imports (importsfromdisk/all-imports bases-src-dir)]
    {:type "base"
     :name base-name
     :imports imports}))

;(read-base-from-disk "../clojure-polylith-realworld-example-app" "clojure/realworld/" "rest-api")
;(read-base-from-disk "../Nova/project-unicorn" "" "backend")

(defn ns-to-path [ns]
  (let [path (str/replace ns "." "/")]
    (if (str/blank? path)
      ""
      (str path "/"))))

(defn read-bricks-from-disk [ws-path top-ns]
  (let [top-src-dir (ns-to-path top-ns)
        component-names (file/directory-paths (str ws-path "/components"))
        components (mapv #(read-component-from-disk ws-path top-src-dir %) component-names)
        base-names (file/directory-paths (str ws-path "/bases"))
        bases (mapv #(read-base-from-disk ws-path top-src-dir %) base-names)]
    {:components components
     :bases bases}))


;(read-bricks-from-disk "../clojure-polylith-realworld-example-app" "clojure.realworld")


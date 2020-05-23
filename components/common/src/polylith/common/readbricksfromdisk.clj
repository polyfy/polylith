(ns polylith.common.readbricksfromdisk
  (:require [clojure.string :as str]
            [polylith.file.interface :as file]))

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
        ; Only one folder should be in each components base src folder. The name of the folder will be
        ; the name of the interface, in case the component's name is not same as it's interface.
        interface-name (first (file/directory-names component-src-dir))
        src-dir (str component-src-dir "/" interface-name)
        interface-file-content (file/read-file (str src-dir "/interface.clj"))
        imports (filter-imports interface-file-content)
        declarations (filter-declarations interface-file-content)
        declarations-infos (vec (sort-by (juxt :type :name) (map ->declarations-info declarations)))]
    {:type      :component
     :name      component-name
     :imports   imports
     :interface {:name         interface-name
                 :declarations declarations-infos}}))

(defn read-bases-from-disk [ws-path top-src-dir base-name]
  (let [base-src-dir (str ws-path "/bases/" base-name "/src/" top-src-dir)
        src-dir (str base-src-dir "/" (str/replace base-name "-" "_"))
        interface-file-content (file/read-file (str src-dir "/interface.clj"))
        imports (filter-imports interface-file-content)]
    {:type :base
     :name base-name
     :imports imports}))

(defn filter-paths [all paths prefix]
  (filterv #(contains? all %)
           (into #{} (map #(-> %
                               (str/replace prefix "")
                               (str/split #"\/")
                               (second))
                          (filter #(str/starts-with? % prefix) paths)))))

(defn all-bases-from-disk
  ([ws-path paths]
   (let [prefix    (str ws-path "/bases")
         all-bases (file/directory-names prefix)]
     (if paths
       (filter-paths all-bases paths prefix)
       all-bases)))
  ([ws-path]
   (all-bases-from-disk ws-path nil)))

(defn all-components-from-disk
  ([ws-path paths]
   (let [prefix (str ws-path "/components")
         all-components (file/directory-names prefix)]
     (if paths
       (filter-paths all-components paths prefix)
       all-components)))
  ([ws-path]
   (all-components-from-disk ws-path nil)))

(defn read-components-and-bases-from-disk [ws-path top-namespace]
  (let [all-component-names (all-components-from-disk ws-path)
        all-base-names (all-bases-from-disk ws-path)
        top-src-dir (str/replace top-namespace #"\." "/")
        components (vec (sort-by :name (map #(read-component-from-disk ws-path top-src-dir %) all-component-names)))
        bases (vec (sort-by :name (map #(read-bases-from-disk ws-path top-src-dir %) all-base-names)))]
    {:components components
     :bases bases}))

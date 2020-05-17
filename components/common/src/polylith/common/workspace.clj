(ns polylith.common.workspace
  (:require [clojure.string :as str]
            [polylith.file.interface :as file]
            [polylith.file.interface :as pfile]
            [clojure.pprint :as pp]))

(defn require? [val]
  (and (sequential? val)
       (= :require (first val))))

(defn ->imports [imports]
  (rest (first (filter require? imports))))

(defn filter-imports [code]
  (mapv first (filterv #(= :as (second %))
                       (->imports (first code)))))

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
     :imports}))

(defn alias->service-or-environment [[k {:keys [extra-paths extra-deps]}] paths deps]
  (let [type (-> k namespace keyword)
        name (name k)
        all-paths (into #{} (concat paths extra-paths))
        all-deps (merge deps extra-deps)
        components (sort-by :name (into #{} (map #(hash-map :type :component
                                                            :name (-> %
                                                                      (str/replace #"components/" "")
                                                                      (str/replace #"/src" "")
                                                                      (str/replace #"/resources" "")))
                                                 (filter #(str/starts-with? % "components/") all-paths))))
        bases (sort-by :name (into #{} (map #(hash-map :type :base
                                                       :name (-> %
                                                                 (str/replace #"bases/" "")
                                                                 (str/replace #"/src" "")
                                                                 (str/replace #"/resources" "")))
                                            (filter #(str/starts-with? % "bases/") all-paths))))
        extra-paths (sort (into #{} (filter #(and (not (str/starts-with? % "components/"))
                                                  (not (str/starts-with? % "bases/")))
                                            all-paths)))]
    {:type         type
     :name         name
     :components   (vec components)
     :bases        (vec bases)
     :extra-paths  (vec extra-paths)
     :dependencies all-deps}))

(def alias-namespaces #{"service" "env"})

(defn polylith-aliases [{:keys [paths deps aliases]}]
  (let [polylith-aliases (filter #(contains? alias-namespaces (-> % key namespace)) aliases)]
    (vec (sort-by (juxt :type :name) (map #(alias->service-or-environment % paths deps) polylith-aliases)))))

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

(defn read-workspace-from-disk [ws-path {:keys [polylith] :as deps}]
  (let [all-component-names (all-components-from-disk ws-path)
        all-base-names (all-bases-from-disk ws-path)
        top-src-dir (str/replace (:top-namespace polylith) #"\." "/")
        components (vec (sort-by :name (map #(read-component-from-disk ws-path top-src-dir %) all-component-names)))
        bases (vec (sort-by :name (map #(read-bases-from-disk ws-path top-src-dir %) all-base-names)))]
    {:polylith   polylith
     :components components
     :bases      bases
     :aliases    (polylith-aliases deps)}))

;(read-workspace-from-disk "." {:polylith {:top-namespace "polylith"}})
;(read-workspace-from-disk "../clojure-polylith-realworld-example-app" {:polylith {:top-namespace "clojure.realworld"}})




;(def deps (-> "/Users/furkan/Workspace/clojure-polylith-realworld-example-app/deps.edn" slurp read-string))
;
;(with-open [writer (io/writer (io/file "polylith.edn"))]
;  (clojure.pprint/pprint (create-polylith-map "/Users/furkan/Workspace/clojure-polylith-realworld-example-app" deps) writer))
;
;(common/extract-aliases deps)
;(polylith-aliases deps)
;(name :a/bb)



;(def ws-path ".")
;(def component-name "common")
;(def base-src-folder "polylith")
;;
;(def component-base-src-folder (str ws-path "/components/" component-name "/src/" base-src-folder))
;(def interface-name (first (file/directory-names component-base-src-folder)))
;(def component-src-folder (str component-base-src-folder "/" interface-name))
;(def interface-file-content (file/read-file (str component-src-folder "/interface.clj")))
;(def declarations (filter-declarations interface-file-content))
;(def declarations-infos (vec (sort-by (juxt :type :name) (map ->declarations-info declarations))))
;
;(pp/pprint interface-file-content)
;
;(map str (pfile/files-recursively "./components/common"))


;"./components/common/src/polylith/common"


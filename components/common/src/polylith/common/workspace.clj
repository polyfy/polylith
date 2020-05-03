(ns polylith.common.workspace
  (:require [polylith.common.interface :as common]
            [clojure.string :as str]
            [polylith.file.interface :as file]))

(defn def-or-defn? [code]
  (if (list? code)
    (let [f (first code)]
      (or (= f 'def) (= f 'defn)))
    false))

(defn read-declarations [path]
  (filterv def-or-defn?
           ; Drops the namespace declaration on top of the file
           (drop 1 (file/read-file path))))

(defn ->declarations-info [statement]
  "Takes a statement (def or defn) from source code,
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

(defn component-name->component [ws-path base-src-folder component-name]
  (let [component-base-src-folder (str ws-path "/components/" component-name "/src/" base-src-folder)
        ; Only one folder should be in each components base src folder. The name of the folder will be
        ; the name of the interface, in case the component's name is not same as it's interface.
        interface-name (first (file/directory-names component-base-src-folder))
        component-src-folder (str component-base-src-folder "/" interface-name)
        interface-file-path (str component-src-folder "/interface.clj")
        declarations (read-declarations interface-file-path)
        declarations-infos (vec (sort-by (juxt :type :name) (map ->declarations-info declarations)))]
    {:type      :component
     :name      component-name
     :interface {:name       interface-name
                 :declarations declarations-infos}}))

(defn base-name->base [ws-path base-src-folder base-name]
  {:type :base
   :name base-name})

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

(defn create-workspace-map [ws-path {:keys [polylith] :as deps}]
  (let [all-component-names (common/all-components-from-disk ws-path)
        all-base-names (common/all-bases-from-disk ws-path)
        base-src-folder (str/replace (:top-namespace polylith) #"\." "/")
        components (vec (sort-by :name (map #(component-name->component ws-path base-src-folder %) all-component-names)))
        bases (vec (sort-by :name (map #(base-name->base ws-path base-src-folder %) all-base-names)))]
    {:polylith   polylith
     :components components
     :bases      bases
     :aliases    (polylith-aliases deps)}))


;(create-workspace-map "." {:polylith {:top-namespace "polylith"}})
;
;(def deps (-> "/Users/furkan/Workspace/clojure-polylith-realworld-example-app/deps.edn" slurp read-string))
;
;(with-open [writer (io/writer (io/file "polylith.edn"))]
;  (clojure.pprint/pprint (create-polylith-map "/Users/furkan/Workspace/clojure-polylith-realworld-example-app" deps) writer))
;
;(common/extract-aliases deps)
;(polylith-aliases deps)
;(name :a/bb)
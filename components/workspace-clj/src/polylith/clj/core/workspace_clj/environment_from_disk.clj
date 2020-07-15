(ns polylith.clj.core.workspace-clj.environment-from-disk
  (:require [clojure.string :as str]
            [clojure.tools.deps.alpha.util.maven :as mvn]
            [polylith.clj.core.workspace-clj.namespaces-from-disk :as ns-from-disk]
            [polylith.clj.core.file.interfc :as file]
            [polylith.clj.core.util.interfc :as util]))

(defn key-as-string [[lib version]]
  [(str lib) version])

(defn sort-deps [deps]
  (apply array-map
         (mapcat identity
                 (sort (map key-as-string deps)))))

(defn brick-name [path start-index]
  (let [end-index (+ start-index (str/index-of (subs path start-index) "/"))]
    (if (< end-index 0)
      path
      (subs path start-index end-index))))

(defn component-name [path]
  (brick-name path 17))

(defn base-name [path]
  (brick-name path 12))

(defn starts-with [path start]
  (and (string? path)
       (str/starts-with? path start)))

(defn component? [path]
  (starts-with path "../../components/"))

(defn base? [path]
  (starts-with path "../../bases/"))

(defn has-src-dir? [paths]
  (not (empty? (filter #(= "src" %) paths))))

(defn has-test-dir? [test-paths]
  (not (empty? (filter #(= "test" %) test-paths))))

(defn read-environment
  ([ws-path env]
   (let [env-path (str ws-path "/environments/" env)
         path (str env-path "/deps.edn")
         {:keys [paths deps aliases mvn/repos]} (read-string (slurp path))
         maven-repos (merge mvn/standard-repos repos)]
     (read-environment env env-path paths deps aliases maven-repos)))
  ([env env-path paths deps aliases maven-repos]
   (let [component-names (vec (sort (set (mapv component-name (filter component? paths)))))
         base-names (vec (sort (set (mapv base-name (filter base? paths)))))
         test-paths (vec (sort (set (-> aliases :test :extra-paths))))
         test-deps (sort-deps (-> aliases :test :extra-deps))
         test-component-names (vec (sort (set (mapv component-name (filter component? test-paths)))))
         test-base-names (vec (sort (set (mapv base-name (filter base? test-paths)))))
         namespaces-src (ns-from-disk/namespaces-from-disk (str env-path "/src"))
         namespaces-test (ns-from-disk/namespaces-from-disk (str env-path "/test"))]
     (util/ordered-map :name env
                       :type "environment"
                       :component-names component-names
                       :test-component-names test-component-names
                       :base-names base-names
                       :test-base-names test-base-names
                       :paths paths
                       :test-paths test-paths
                       :lib-deps (sort-deps deps)
                       :test-deps test-deps
                       :maven-repos maven-repos
                       :has-src-dir? (has-src-dir? paths)
                       :has-test-dir? (has-test-dir? test-paths)
                       :namespaces-src namespaces-src
                       :namespaces-test namespaces-test))))

(defn read-environments [ws-path]
  (let [env-dirs (file/directory-paths (str ws-path "/environments"))]
    (mapv #(read-environment ws-path %) env-dirs)))

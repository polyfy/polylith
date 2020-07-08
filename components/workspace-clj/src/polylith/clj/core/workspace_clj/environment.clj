(ns polylith.clj.core.workspace-clj.environment
  (:require [clojure.string :as str]
            [clojure.tools.deps.alpha.util.maven :as mvn]
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

(defn component? [path]
  (and (string? path)
       (str/starts-with? path "../../components/")))

(defn base? [path]
  (and (string? path)
       (str/starts-with? path "../../bases/")))

(defn environment
  ([ws-path env]
   (let [path (str ws-path "/environments/" env "/deps.edn")
         {:keys [paths deps aliases mvn/repos]} (read-string (slurp path))
         maven-repos (merge mvn/standard-repos repos)]
     (environment env paths deps aliases maven-repos)))
  ([env paths deps aliases maven-repos]
   (let [component-names (vec (sort (set (mapv component-name (filter component? paths)))))
         base-names (vec (sort (set (mapv base-name (filter base? paths)))))
         test-paths (vec (sort (set (-> aliases :test :extra-paths))))
         test-deps (sort-deps (-> aliases :test :extra-deps))
         test-component-names (vec (sort (set (mapv component-name (filter component? test-paths)))))
         test-base-names (vec (sort (set (mapv base-name (filter base? test-paths)))))]
     (util/ordered-map :name env
                       :type "environment"
                       :component-names component-names
                       :test-component-names test-component-names
                       :base-names base-names
                       :test-base-names test-base-names
                       :paths paths
                       :test-paths test-paths
                       :deps (sort-deps deps)
                       :test-deps test-deps
                       :maven-repos maven-repos))))

(defn environments [ws-path]
  (let [env-dirs (file/directory-paths (str ws-path "/environments"))]
    (mapv #(environment ws-path %) env-dirs)))

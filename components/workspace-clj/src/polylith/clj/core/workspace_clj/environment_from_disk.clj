(ns polylith.clj.core.workspace-clj.environment-from-disk
  (:require [clojure.string :as str]
            [clojure.tools.deps.alpha.util.maven :as mvn]
            [polylith.clj.core.file.interfc :as file]
            [polylith.clj.core.util.interfc :as util]
            [polylith.clj.core.workspace-clj.namespaces-from-disk :as ns-from-disk]))

(defn absolute-path [path env]
  (cond
    (str/starts-with? path "./") (str "environments/" env "/" (subs path 2))
    (str/starts-with? path "../../") (subs path 6)
    :else (str "environments/" env "/" path)))

(defn absolute-paths [env paths dev-env?]
  (let [sorted-paths (-> paths sort vec)]
    (if dev-env?
      sorted-paths
      (mapv #(absolute-path % env) sorted-paths))))

(defn read-environment
  ([{:keys [env env-dir config-file dev?]}]
   (let [{:keys [paths deps aliases mvn/repos]} (read-string (slurp config-file))
         maven-repos (merge mvn/standard-repos repos)]
     (read-environment env env-dir config-file dev? paths deps aliases maven-repos)))
  ([env env-dir config-file dev? paths deps aliases maven-repos]
   (let [src-paths (if dev? (-> aliases :dev :extra-paths) paths)
         lib-deps (if dev? (-> aliases :dev :extra-deps) deps)
         absolute-src-paths (absolute-paths env src-paths dev?)
         test-paths (-> aliases :test :extra-paths)
         absolute-test-paths (absolute-paths env test-paths dev?)
         test-lib-deps (util/stringify-and-sort-map (-> aliases :test :extra-deps))
         namespaces-src (ns-from-disk/namespaces-from-disk (str env-dir "/src"))
         namespaces-test (ns-from-disk/namespaces-from-disk (str env-dir "/test"))]
     (util/ordered-map :name env
                       :dev? dev?
                       :env-dir env-dir
                       :config-file config-file
                       :type "environment"
                       :src-paths absolute-src-paths
                       :test-paths absolute-test-paths
                       :lib-deps (util/stringify-and-sort-map lib-deps)
                       :test-lib-deps test-lib-deps
                       :maven-repos maven-repos
                       :namespaces-src namespaces-src
                       :namespaces-test namespaces-test))))

(defn env-map [ws-dir env]
  {:env env
   :dev? false
   :env-dir (str ws-dir "/environments/" env)
   :config-file (str ws-dir "/environments/" env "/deps.edn")})

(defn read-environments [ws-dir]
  (let [env-configs (conj (map #(env-map ws-dir %)
                               (file/directory-paths (str ws-dir "/environments")))
                          {:env "development"
                           :dev? true
                           :env-dir (str ws-dir "/development")
                           :config-file (str ws-dir "/deps.edn")})]
    (mapv read-environment env-configs)))

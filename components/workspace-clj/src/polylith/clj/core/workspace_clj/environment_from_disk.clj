(ns polylith.clj.core.workspace-clj.environment-from-disk
  (:require [clojure.string :as str]
            [clojure.tools.deps.alpha.util.maven :as mvn]
            [polylith.clj.core.file.interface :as file]
            [polylith.clj.core.lib.interface :as lib]
            [polylith.clj.core.util.interface :as util]
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
  ([{:keys [env env-dir config-file is-dev]} user-home]
   (let [{:keys [paths deps aliases mvn/repos]} (read-string (slurp config-file))
         maven-repos (merge mvn/standard-repos repos)]
     (read-environment env env-dir config-file is-dev paths deps aliases maven-repos user-home)))
  ([env env-dir config-file is-dev paths deps aliases maven-repos user-home]
   (let [src-paths (if is-dev (-> aliases :dev :extra-paths) paths)
         lib-deps (lib/with-sizes (if is-dev (-> aliases :dev :extra-deps) deps) user-home)
         test-lib-deps (lib/with-sizes (-> aliases :test :extra-deps) user-home)
         absolute-src-paths (absolute-paths env src-paths is-dev)
         test-paths (-> aliases :test :extra-paths)
         absolute-test-paths (absolute-paths env test-paths is-dev)
         namespaces-src (ns-from-disk/namespaces-from-disk (str env-dir "/src"))
         namespaces-test (ns-from-disk/namespaces-from-disk (str env-dir "/test"))]
     (util/ordered-map :name env
                       :is-dev is-dev
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
   :is-dev false
   :env-dir (str ws-dir "/environments/" env)
   :config-file (str ws-dir "/environments/" env "/deps.edn")})

(defn read-environments [ws-dir user-home]
  (let [env-configs (conj (map #(env-map ws-dir %)
                               (file/directories (str ws-dir "/environments")))
                          {:env "development"
                           :is-dev true
                           :env-dir (str ws-dir "/development")
                           :config-file (str ws-dir "/deps.edn")})]
    (mapv #(read-environment % user-home) env-configs)))

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
  (brick-name path 11))

(defn base-name [path]
  (brick-name path 6))

(defn starts-with [path start]
  (and (string? path)
       (str/starts-with? path start)))

(defn component? [path]
  (starts-with path "components/"))

(defn base? [path]
  (starts-with path "bases/"))

(defn has-src-dir? [env paths]
  (not (empty? (filter #(= (str "environments/" env "/src") %) paths))))

(defn has-test-dir? [env test-paths]
  (not (empty? (filter #(= (str "environments/" env "/test") %) test-paths))))

(defn file-exists [env-dir path]
  (file/exists (str env-dir "/" path)))

(defn ws-root-path [path env]
  (cond
    (str/starts-with? path "./") (str "environments/" env "/" (subs path 2))
    (str/starts-with? path "../../") (subs path 6)
    :else (str "environments/" env "/" path)))

(defn cleaned-existing-paths [env env-dir paths dev-env?]
  (let [existing-paths (sort (set (filter #(file-exists env-dir %) paths)))]
    (if dev-env?
      (vec existing-paths)
      (mapv #(ws-root-path % env) existing-paths))))

(defn read-environment
  ([{:keys [env env-dir current-dir config-file dev-env?]}]
   (let [{:keys [paths deps aliases mvn/repos]} (read-string (slurp config-file))
         maven-repos (merge mvn/standard-repos repos)]
     (read-environment env env-dir current-dir config-file dev-env? paths deps aliases maven-repos)))
  ([env env-dir current-dir config-file dev-env? paths deps aliases maven-repos]
   (let [src-paths (if dev-env? (-> aliases :dev :extra-paths) paths)
         lib-deps (if dev-env? (-> aliases :dev :extra-deps) deps)
         cleaned-src-paths (cleaned-existing-paths env current-dir src-paths dev-env?)
         component-names (vec (sort (set (mapv component-name (filter component? cleaned-src-paths)))))
         base-names (vec (sort (set (mapv base-name (filter base? cleaned-src-paths)))))
         cleaned-test-paths (cleaned-existing-paths env current-dir (-> aliases :test :extra-paths) dev-env?)
         test-component-names (vec (sort (set (mapv component-name (filter component? cleaned-test-paths)))))
         test-base-names (vec (sort (set (mapv base-name (filter base? cleaned-test-paths)))))
         test-deps (sort-deps (-> aliases :test :extra-deps))
         namespaces-src (ns-from-disk/namespaces-from-disk (str env-dir "/src"))
         namespaces-test (ns-from-disk/namespaces-from-disk (str env-dir "/test"))]
     (util/ordered-map :name env
                       :env-dir env-dir
                       :config-file config-file
                       :type "environment"
                       :component-names component-names
                       :test-component-names test-component-names
                       :base-names base-names
                       :test-base-names test-base-names
                       :src-paths cleaned-src-paths
                       :test-paths cleaned-test-paths
                       :lib-deps (sort-deps lib-deps)
                       :test-deps test-deps
                       :maven-repos maven-repos
                       :has-src-dir? (has-src-dir? env cleaned-src-paths)
                       :has-test-dir? (has-test-dir? env cleaned-test-paths)
                       :namespaces-src namespaces-src
                       :namespaces-test namespaces-test))))

(defn env-map [ws-dir env]
  {:env env
   :dev-env? false
   :env-dir (str ws-dir "/environments/" env)
   :current-dir (str ws-dir "/environments/" env)
   :config-file (str ws-dir "/environments/" env "/deps.edn")})

(defn read-environments [ws-dir]
  (let [env-configs (conj (map #(env-map ws-dir %)
                               (file/directory-paths (str ws-dir "/environments")))
                          {:env "development"
                           :dev-env? true
                           :env-dir (str ws-dir "/development")
                           :current-dir ws-dir
                           :config-file (str ws-dir "/deps.edn")})]
    (mapv read-environment env-configs)))

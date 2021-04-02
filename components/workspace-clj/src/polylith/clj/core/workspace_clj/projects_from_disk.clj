(ns polylith.clj.core.workspace-clj.projects-from-disk
  (:require [clojure.set :as set]
            [clojure.string :as str]
            [clojure.tools.deps.alpha.util.maven :as mvn]
            [polylith.clj.core.file.interface :as file]
            [polylith.clj.core.lib.interface :as lib]
            [polylith.clj.core.util.interface :as util]
            [polylith.clj.core.util.interface.color :as color]
            [polylith.clj.core.validator.interface :as validator]
            [polylith.clj.core.workspace-clj.namespaces-from-disk :as ns-from-disk]))

(defn absolute-path [path project-name]
  (cond
    (str/starts-with? path "./") (str "projects/" project-name "/" (subs path 2))
    (str/starts-with? path "../../") (subs path 6)
    :else (str "projects/" project-name "/" path)))

(defn brick-path? [path]
  (and path
       (or
         (str/starts-with? path "../../bases/")
         (str/starts-with? path "../../components/"))))

(defn brick? [[_ {:keys [local/root]}]]
  (and (-> root nil? not)
       (brick-path? root)))

(defn deps-and-paths-from-brick [path project-name project-dir include-src?]
  (let [brick-path (absolute-path path project-name)
        config (read-string (slurp (str project-dir "/" path "/deps.edn")))
        src-paths (mapv #(str brick-path "/" %)
                        (:paths config))
        test-paths (cond-> (mapv #(str brick-path "/" %)
                                 (-> config :aliases :test :extra-paths))
                           include-src? (concat src-paths))
        src-deps (:deps config)
        test-deps (cond-> (-> config :aliases :test :extra-deps)
                          include-src? (merge src-deps))]
    {:src-paths src-paths
     :test-paths test-paths
     :src-deps src-deps
     :test-deps test-deps}))

(defn all-paths [is-dev dev-type src-type deps-type aliases project-name project-dir user-home brick-paths deps paths include-src?]
  (let [deps-and-paths (map #(deps-and-paths-from-brick % project-name project-dir include-src?) brick-paths)
        paths (vec (sort (set (if is-dev (-> aliases dev-type :extra-paths)
                                         (concat (map #(absolute-path % project-name) paths)
                                                 (mapcat src-type deps-and-paths))))))
        lib-deps (lib/with-sizes (if is-dev (-> aliases dev-type :extra-deps)
                                            (concat (filter (complement brick?) deps)
                                                    (mapcat deps-type deps-and-paths))) user-home)]
    [paths lib-deps]))

(defn brick-path [[_ entry]]
  (let [path (:local/root entry)]
    (when (brick-path? path)
      [path])))

(defn read-project
  ([{:keys [project-name project-dir config-file is-dev]} ws-type user-home color-mode]
   (let [{:keys [paths deps aliases mvn/repos] :as config} (read-string (slurp config-file))
         maven-repos (merge mvn/standard-repos repos)
         message (when (not is-dev) (validator/validate-project-deployable-config ws-type config))]
     (if message
       (throw (ex-info (str "  " (color/error color-mode (str "Error in " config-file ": ") message)) message))
       (read-project project-name project-dir config-file is-dev paths deps aliases maven-repos user-home))))
  ([project-name project-dir config-file is-dev paths deps aliases maven-repos user-home]
   (let [brick-paths (set (mapcat brick-path deps))
         [src-paths lib-deps] (all-paths is-dev :dev :src-paths :src-deps aliases project-name project-dir user-home brick-paths deps paths false)
         test-brick-paths (set (mapcat brick-path (-> aliases :test :extra-deps)))
         ;; If the brick only exists as a test dependency, then also include the brick's src paths and library dependencies.
         include-src? (empty? (set/intersection brick-paths test-brick-paths))
         [test-paths1 lib-deps-test1] (all-paths is-dev :dev :test-paths :test-deps aliases project-name project-dir user-home brick-paths deps paths false)
         [test-paths2 lib-deps-test2] (all-paths is-dev :test :test-paths :test-deps aliases project-name project-dir user-home
                                                 test-brick-paths
                                                 (-> aliases :test :extra-deps)
                                                 (-> aliases :test :extra-paths)
                                                 include-src?)
         test-paths (vec (concat test-paths1 test-paths2))
         lib-deps-test (merge lib-deps-test1 lib-deps-test2)
         namespaces-src (ns-from-disk/namespaces-from-disk (str project-dir "/src"))
         namespaces-test (ns-from-disk/namespaces-from-disk (str project-dir "/test"))]
     (util/ordered-map :name project-name
                       :is-dev is-dev
                       :project-dir project-dir
                       :config-file config-file
                       :type "project"
                       :src-paths src-paths
                       :test-paths test-paths
                       :lib-deps lib-deps
                       :lib-deps-test lib-deps-test
                       :maven-repos maven-repos
                       :namespaces-src namespaces-src
                       :namespaces-test namespaces-test))))

(defn project-map [ws-dir project-name]
  {:project-name project-name
   :is-dev false
   :project-dir (str ws-dir "/projects/" project-name)
   :config-file (str ws-dir "/projects/" project-name "/deps.edn")})

(defn read-projects [ws-dir ws-type user-home color-mode]
  (let [project-configs (conj (map #(project-map ws-dir %)
                                   (file/directories (str ws-dir "/projects")))
                              {:project-name "development"
                               :is-dev true
                               :project-dir (str ws-dir "/development")
                               :config-file (str ws-dir "/deps.edn")})]
    (mapv #(read-project % ws-type user-home color-mode) project-configs)))

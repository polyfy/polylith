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

(defn absolute-path [path project-name is-dev]
  (if is-dev
    path
    (cond
      (str/starts-with? path "./") (str "projects/" project-name "/" (subs path 2))
      (str/starts-with? path "../../") (subs path 6)
      :else (str "projects/" project-name "/" path))))

(defn brick-path? [path is-dev]
  (let [prefix (if is-dev "" "../../")]
    (and path
         (or
           (str/starts-with? path (str prefix "bases/"))
           (str/starts-with? path (str prefix "components/"))))))

(defn brick? [[_ {:keys [local/root]}] is-dev]
  (and (-> root nil? not)
       (brick-path? root is-dev)))

(defn brick-paths-and-deps
  "Given a path, e.g. '../../components/invoicer', parse its deps.edn file
   and return the paths and dependencies that are defined in it."
  [path project-name project-config-dir is-dev]
  (let [brick-path (absolute-path path project-name is-dev)
        brick-config-path (str project-config-dir "/" path "/deps.edn")
        brick-config (-> brick-config-path slurp read-string)
        src-paths (mapv #(str brick-path "/" %)
                        (:paths brick-config))
        test-paths (mapv #(str brick-path "/" %)
                         (-> brick-config :aliases :test :extra-paths))
        src-deps (:deps brick-config)
        test-deps (or (-> brick-config :aliases :test :extra-deps) {})]
    {:src-paths src-paths
     :test-paths test-paths
     :src-deps src-deps
     :test-deps test-deps}))

(defn extract-brick-path
  "Returns the path from a dependency if it's a valid path to a brick."
  [[_ entry] is-dev]
  (let [path (:local/root entry)]
    (when (brick-path? path is-dev)
      [path])))

(defn override-src-deps
  "Override the dependencies specified by :override-deps."
  [override-deps src-deps]
  (if override-deps
    (reduce-kv
      (fn [m k v]
        (assoc m k (override-deps k v)))
      {} (into {} src-deps))
    src-deps))

(defn src-paths-and-libs-from-bricks
  "Returns all src paths and src dependencies that are included from the src context
   for a given project deps.edn file, including:
   - project paths that are specified as :paths or :aliases > :dev > :extra-paths (development)
     in the project's deps.edn file.
   - brick :src paths that are specified in :deps or :aliases > :dev > :extra-deps (development)
     as :local/root in the project's deps.edn file and extracted from the corresponding
     brick deps.edn files.
   - brick :src libraries that are specified in :deps or :aliases > :dev > :extra-deps (development)
     in the project's deps.edn file.
   - brick :src libraries that are specified in :deps or :aliases > :dev > :extra-deps (development)
     as :local/root in the project's deps.edn file and extracted from the corresponding
     brick deps.edn files.
   If :override-deps is given, then library versions will be overridden."
  [is-dev project-name project-config-dir user-home project-src-deps project-src-paths override-deps]
  (let [brick-src-paths (set (mapcat #(extract-brick-path % is-dev) project-src-deps))
        src-deps-and-paths (map #(brick-paths-and-deps % project-name project-config-dir is-dev) brick-src-paths)
        paths (vec (sort (set (concat (map #(absolute-path % project-name is-dev) project-src-paths)
                                      (mapcat :src-paths src-deps-and-paths)))))
        src-deps (override-src-deps override-deps
                                    (mapcat :src-deps src-deps-and-paths))
        lib-deps (lib/with-sizes (concat (filter #(not (brick? % is-dev)) project-src-deps)
                                         src-deps) user-home)]
    [paths lib-deps]))

(defn skip-all-tests? [bricks-to-test]
  (and (-> bricks-to-test nil? not)
       (empty? bricks-to-test)))

(defn test-paths-and-libs-from-bricks
  "Returns all test paths and test dependencies that are included from the test context
   for a given project deps.edn file, including:
   - project paths that are specified as :aliases > :test > :extra-paths in the project's deps.edn file.
   - brick :test paths that are specified in :aliases > :src > :extra-deps as :local/root in the
     project's deps.edn file and extracted from the corresponding brick deps.edn files.
   - brick :test paths that are specified in :aliases > :test > :extra-deps as :local/root in the
     project's deps.edn file and extracted from the corresponding brick deps.edn files.
   - brick :src paths that are specified in :aliases > :test > :extra-deps as :local/root but not in
     :aliases > :src > :extra-deps, are extracted from the corresponding brick deps.edn files.
     This is needed when a brick is only added to the test context but not to the source context,
     so that we have access to the brick under test.
   - brick :test libraries that are specified in :aliases > :test > :extra-deps in the project's deps.edn file.
   - brick :test libraries that are specified in :aliases > :dev > :extra-deps as :local/root
     in the project's deps.edn file and extracted from the corresponding brick deps.edn files.
   - brick :src libraries that are specified in :aliases > :test > :extra-deps as :local/root but not in
     :aliases > :src > :extra-deps, are extracted from the corresponding brick deps.edn files.
   If :override-deps is given, then library versions will be overridden."
  [is-dev project-name project-config-dir bricks-to-test user-home project-src-deps project-test-deps project-test-paths override-deps]
  ;; todo: support filtering on individual bricks.
  (if (skip-all-tests? bricks-to-test)
    [[] []]
    (let [brick-src-paths (set (mapcat #(extract-brick-path % is-dev) project-src-deps))
          brick-test-paths (set (mapcat #(extract-brick-path % is-dev) project-test-deps))
          src-deps-and-paths (map #(brick-paths-and-deps % project-name project-config-dir is-dev) brick-src-paths)
          only-test-brick-paths (set/difference brick-test-paths brick-src-paths)
          test-deps-and-paths (map #(brick-paths-and-deps % project-name project-config-dir is-dev) brick-test-paths)
          only-test-paths (map #(brick-paths-and-deps % project-name project-config-dir is-dev) only-test-brick-paths)
          paths (concat (map #(absolute-path % project-name is-dev) project-test-paths)
                        (mapcat :test-paths src-deps-and-paths)
                        (mapcat :test-paths test-deps-and-paths)
                        (mapcat :src-paths only-test-paths))
          test-deps (override-src-deps override-deps
                                       (concat (mapcat :test-deps src-deps-and-paths)
                                               (mapcat :test-deps test-deps-and-paths)
                                               (mapcat :src-deps only-test-paths)))
          lib-deps (lib/with-sizes (concat (filter #(not (brick? % is-dev)) project-test-deps)
                                           test-deps) user-home)]
      [(vec (sort (set paths)))
       (vec (sort (set lib-deps)))])))

(defn read-project
  ([{:keys [project-name project-dir project-config-dir is-dev]} ws-type project->settings user-home color-mode]
   (let [config-filename (str project-config-dir "/deps.edn")
         {:keys [paths deps override-deps aliases mvn/repos] :as config} (read-string (slurp config-filename))
         project-src-paths (if is-dev (-> aliases :dev :extra-paths) paths)
         project-src-deps (if is-dev (-> aliases :dev :extra-deps) deps)
         project-test-paths (-> aliases :test :extra-paths)
         project-test-deps (-> aliases :test :extra-deps)
         maven-repos (merge mvn/standard-repos repos)
         message (when (not is-dev) (validator/validate-project-deployable-config ws-type config))]
     (if message
       (throw (ex-info (str "  " (color/error color-mode (str "Error in " config-filename ": ") message)) message))
       (read-project project-name project-dir project-config-dir config-filename is-dev maven-repos project->settings
                     user-home project-src-paths project-src-deps project-test-paths project-test-deps override-deps))))
  ([project-name project-dir project-config-dir config-filename is-dev maven-repos project->settings user-home project-src-paths project-src-deps project-test-paths project-test-deps override-deps]
   (let [[src-paths src-lib-deps] (src-paths-and-libs-from-bricks is-dev project-name project-config-dir user-home project-src-deps project-src-paths override-deps)
         bricks-to-test (-> project-name project->settings :test)
         [test-paths test-lib-deps] (test-paths-and-libs-from-bricks is-dev project-name project-config-dir bricks-to-test user-home project-src-deps project-test-deps project-test-paths override-deps)
         paths (cond-> {}
                       (seq src-paths) (assoc :src src-paths)
                       (seq test-paths) (assoc :test test-paths))
         lib-deps (cond-> {}
                          (seq src-lib-deps) (assoc :src src-lib-deps)
                          (seq test-lib-deps) (assoc :test test-lib-deps))
         namespaces (ns-from-disk/namespaces-from-disk (str project-dir "/src") (str project-dir "/test"))]
     (util/ordered-map :name project-name
                       :is-dev is-dev
                       :project-dir project-dir
                       :config-filename config-filename
                       :type "project"
                       :paths paths
                       :lib-deps lib-deps
                       :maven-repos maven-repos
                       :namespaces namespaces))))

(defn project-map [ws-dir project-name]
  {:project-name project-name
   :is-dev false
   :project-dir (str ws-dir "/projects/" project-name)
   :project-config-dir (str ws-dir "/projects/" project-name)})

(defn keep?
  "Skip projects that are passed in as e.g. skip:P1:P2."
  [{:keys [project-name]} project->settings skip]
  (not (or (contains? skip project-name)
           (contains? skip (-> project-name project->settings :alias)))))

(defn read-projects [ws-dir ws-type project->settings user-input user-home color-mode]
  (let [skip (if user-input (-> user-input :skip set) #{})
        project-configs (filter #(keep? % project->settings skip)
                                (conj (map #(project-map ws-dir %)
                                           (file/directories (str ws-dir "/projects")))
                                      {:project-name "development"
                                       :is-dev true
                                       :project-dir (str ws-dir "/development")
                                       :project-config-dir ws-dir}))]
    (mapv #(read-project % ws-type project->settings user-home color-mode) project-configs)))

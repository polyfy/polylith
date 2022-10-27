(ns polylith.clj.core.workspace-clj.projects-from-disk
  (:require [clojure.set :as set]
            [clojure.string :as str]
            [clojure.tools.deps.alpha.util.maven :as mvn]
            [polylith.clj.core.common.interface.config :as config]
            [polylith.clj.core.file.interface :as file]
            [polylith.clj.core.lib.interface :as lib]
            [polylith.clj.core.util.interface :as util]
            [polylith.clj.core.validator.interface :as validator]
            [polylith.clj.core.workspace-clj.brick-deps :as brick-deps]
            [polylith.clj.core.workspace-clj.namespaces-from-disk :as ns-from-disk]
            [polylith.clj.core.workspace-clj.project-paths :as project-paths]))

(defn absolute-path [path project-name is-dev]
  (if is-dev
    path
    (cond
      (str/starts-with? path "./") (str "projects/" project-name "/" (subs path 2))
      (str/starts-with? path "../../") (subs path 6)
      :else (str "projects/" project-name "/" path))))

(defn brick-path? [path is-dev]
  (if is-dev
    (or
      (str/starts-with? path "bases/")
      (str/starts-with? path "components/")
      (str/starts-with? path "./bases/")
      (str/starts-with? path "./components/"))
    (or
      (str/starts-with? path "../../bases/")
      (str/starts-with? path "../../components/"))))

(defn brick? [[_ {:keys [local/root]}] is-dev]
  (and (-> root nil? not)
       (brick-path? root is-dev)))

(defn ->brick-src-paths [{:keys [name type paths]}]
  (map #(str type "s/" name "/" %)
       (:src paths)))

(defn ->brick-test-paths [{:keys [name type paths]}]
  (map #(str type "s/" name "/" %)
       (:test paths)))

(defn brick-libs [name->brick brick-name type]
  (let [lib-deps (-> brick-name name->brick :lib-deps type)]
    (util/sort-map lib-deps)))

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
  [ws-dir name->brick is-dev project-name user-home project-src-deps project-src-paths override-deps]
  (let [src-brick-names (brick-deps/extract-brick-names is-dev project-src-deps)
        project-src (into (sorted-set) (map #(absolute-path % project-name is-dev)) project-src-paths)
        bricks-src (into (sorted-set) (mapcat #(-> % name->brick ->brick-src-paths)) src-brick-names)
        paths (into project-src bricks-src)
        entity-root-path (when (not is-dev) (str "projects/" project-name))
        lib-deps (-> ws-dir
                     (lib/with-sizes-vec
                      entity-root-path
                      (filterv #(not (brick? % is-dev)) project-src-deps)
                      user-home)
                     (into (mapcat #(brick-libs name->brick % :src)) src-brick-names)
                     (lib/resolve-libs override-deps))]
    [(vec paths) lib-deps]))

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
  [ws-dir name->brick is-dev project-name bricks-to-test user-home project-src-deps project-test-deps project-test-paths override-src-deps override-test-deps]
  ;; todo: support filtering on individual bricks.
  (if (skip-all-tests? bricks-to-test)
    [[] []]
    (let [all-brick-names (brick-deps/extract-brick-names is-dev (concat project-src-deps project-test-deps))
          src-brick-names (brick-deps/extract-brick-names is-dev project-src-deps)
          test-brick-names (brick-deps/extract-brick-names is-dev project-test-deps)
          src-only-brick-names (set/difference test-brick-names src-brick-names)
          paths (-> (sorted-set)
                    (into (map #(absolute-path % project-name is-dev)) project-test-paths)
                    (into (mapcat #(-> % name->brick ->brick-test-paths)) all-brick-names)
                    (into (mapcat #(-> % name->brick ->brick-src-paths)) src-only-brick-names))
          entity-root-path (when (not is-dev) (str "projects/" project-name))
          lib-deps (-> ws-dir
                       (lib/with-sizes-vec
                         entity-root-path
                         (filterv #(not (brick? % is-dev)) project-test-deps)
                         user-home)
                       (into (mapcat #(brick-libs name->brick % :test)) all-brick-names)
                       (into (mapcat #(brick-libs name->brick % :src)) src-only-brick-names)
                       (lib/resolve-libs (merge override-src-deps override-test-deps)))]
      [(vec paths) (vec (sort (set lib-deps)))])))

(defn read-project
  ([{:keys [project-name project-dir project-config-dir is-dev]} ws-dir ws-type name->brick project->settings user-home suffixed-top-ns interface-ns]
   (let [config-filename (str project-config-dir "/deps.edn")
         {:keys [paths deps override-deps aliases mvn/repos] :as config} (config/read-deps-file config-filename)
         project-src-paths (cond-> paths is-dev (concat (-> aliases :dev :extra-paths)))
         project-src-deps (cond-> deps is-dev (merge (-> aliases :dev :extra-deps)))
         project-test-paths (-> aliases :test :extra-paths)
         project-test-deps (-> aliases :test :extra-deps)
         entity-root-path (when (not is-dev) (str "projects/" project-name))
         override-src-deps (lib/latest-with-sizes ws-dir entity-root-path (if is-dev (-> aliases :dev :override-deps) override-deps) user-home)
         override-test-deps (lib/latest-with-sizes ws-dir entity-root-path (-> aliases :test :override-deps) user-home)
         maven-repos (merge mvn/standard-repos repos)
         message (when (not is-dev) (validator/validate-project-deployable-config ws-type config))]
     (if message
       (println (str "Couldn't read the 'deps.edn' file from project '" project-name "': " message))
       (read-project ws-dir name->brick project-name project-dir config-filename is-dev maven-repos
                     project->settings user-home project-src-paths project-src-deps project-test-paths
                     project-test-deps override-src-deps override-test-deps suffixed-top-ns interface-ns))))
  ([ws-dir name->brick project-name project-dir config-filename is-dev maven-repos
    project->settings user-home project-src-paths project-src-deps project-test-paths
    project-test-deps override-src-deps override-test-deps suffixed-top-ns interface-ns]
   (let [[src-paths src-lib-deps] (src-paths-and-libs-from-bricks ws-dir name->brick is-dev project-name user-home project-src-deps project-src-paths override-src-deps)
         bricks-to-test (-> project-name project->settings :test :include)
         [test-paths test-lib-deps] (test-paths-and-libs-from-bricks ws-dir name->brick is-dev project-name bricks-to-test user-home project-src-deps project-test-deps project-test-paths override-src-deps override-test-deps)
         paths (cond-> {}
                       (seq src-paths) (assoc :src src-paths)
                       (seq test-paths) (assoc :test test-paths))
         lib-deps (cond-> {}
                          (seq src-lib-deps) (assoc :src src-lib-deps)
                          (seq test-lib-deps) (assoc :test test-lib-deps))
         {:keys [src-dirs test-dirs]} (project-paths/project-source-dirs ws-dir project-name is-dev project-src-paths project-test-paths)
         namespaces (ns-from-disk/namespaces-from-disk src-dirs test-dirs suffixed-top-ns interface-ns)]
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

(defn read-projects [ws-dir ws-type name->brick project->settings user-input user-home suffixed-top-ns interface-ns]
  (let [skip (if user-input (-> user-input :skip set) #{})
        project-maps (into [{:project-name "development"
                             :is-dev true
                             :project-dir (str ws-dir "/development")
                             :project-config-dir ws-dir}]
                           (map #(project-map ws-dir %))
                           (file/directories (str ws-dir "/projects")))]
    (into []
          (comp (filter #(keep? % project->settings skip))
                (keep #(read-project % ws-dir ws-type name->brick project->settings user-home suffixed-top-ns interface-ns)))
          project-maps)))

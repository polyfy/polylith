(ns ^:no-doc polylith.clj.core.workspace.fromdisk.projects-from-disk
  (:require [clojure.set :as set]
            [clojure.string :as str]
            [clojure.tools.deps.util.maven :as mvn]
            [polylith.clj.core.lib.interface :as lib]
            [polylith.clj.core.util.interface :as util]
            [polylith.clj.core.workspace.fromdisk.brick-name-extractor :as brick-name-extractor]
            [polylith.clj.core.workspace.fromdisk.namespaces-from-disk :as ns-from-disk]
            [polylith.clj.core.workspace.fromdisk.project-paths :as project-paths]))

(defn absolute-path [path project-name is-dev]
  (if is-dev
    path
    (cond
      (str/starts-with? path "./") (str "projects/" project-name "/" (subs path 2))
      (str/starts-with? path "../../") (subs path 6)
      :else (str "projects/" project-name "/" path))))

(defn brick? [[_ {:keys [local/root]}] is-dev]
  (brick-name-extractor/brick-name root is-dev))

(defn ->brick-src-paths [{:keys [name type paths]}]
  (map #(str type "s/" name "/" %)
       (:src paths)))

(defn ->brick-test-paths [{:keys [name type paths]}]
  (map #(str type "s/" name "/" %)
       (:test paths)))

(defn brick-libs [name->brick brick-name type]
  (let [lib-deps (-> brick-name name->brick :lib-deps type)]
    (util/sort-map lib-deps)))

(defn src-paths-from-bricks
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
     brick deps.edn files."
  [project-name is-dev name->brick project-src-paths project-src-deps]
  (let [src-brick-names (brick-name-extractor/brick-names is-dev project-src-deps)
        project-src (into (sorted-set) (map #(absolute-path % project-name is-dev)) project-src-paths)
        bricks-src (into (sorted-set) (mapcat #(-> % name->brick ->brick-src-paths)) src-brick-names)
        paths (into project-src bricks-src)]
    (vec paths)))

(defn src-lib-deps-from-bricks
  "If :override-deps is given, then library versions will be overridden."
  [ws-dir project-name is-dev user-home name->brick project-src-deps override-src-deps]
  (let [src-brick-names (brick-name-extractor/brick-names is-dev project-src-deps)
        entity-root-path (when (not is-dev) (str "projects/" project-name))
        project-lib-deps (lib/with-sizes-vec
                           ws-dir
                           entity-root-path
                           (filterv #(not (brick? % is-dev)) project-src-deps)
                           user-home)
        lib-deps (-> project-lib-deps
                     (into (mapcat #(brick-libs name->brick % :src)) src-brick-names)
                     (lib/resolve-libs override-src-deps))]
    [lib-deps project-lib-deps]))

(defn test-paths-from-bricks
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
     :aliases > :src > :extra-deps, are extracted from the corresponding brick deps.edn files."
  [project-name is-dev name->brick project-test-paths project-src-deps project-test-deps]
  (let [all-brick-names (brick-name-extractor/brick-names is-dev (concat project-src-deps project-test-deps))
        src-brick-names (brick-name-extractor/brick-names is-dev project-src-deps)
        test-brick-names (brick-name-extractor/brick-names is-dev project-test-deps)
        src-only-brick-names (set/difference test-brick-names src-brick-names)
        paths (-> (sorted-set)
                  (into (map #(absolute-path % project-name is-dev)) project-test-paths)
                  (into (mapcat #(-> % name->brick ->brick-test-paths)) all-brick-names)
                  (into (mapcat #(-> % name->brick ->brick-src-paths)) src-only-brick-names))]
    (vec paths)))

(defn test-lib-deps-from-bricks
  "If :override-deps is given, then library versions will be overridden."
  [ws-dir project-name is-dev name->brick user-home project-src-deps project-test-deps override-src-deps override-test-deps]
  (let [all-brick-names (brick-name-extractor/brick-names is-dev (concat project-src-deps project-test-deps))
        src-brick-names (brick-name-extractor/brick-names is-dev project-src-deps)
        test-brick-names (brick-name-extractor/brick-names is-dev project-test-deps)
        src-only-brick-names (set/difference test-brick-names src-brick-names)
        entity-root-path (when (not is-dev) (str "projects/" project-name))
        project-lib-deps (lib/with-sizes-vec
                           ws-dir
                           entity-root-path
                           (filterv #(not (brick? % is-dev)) project-test-deps)
                           user-home)
        lib-deps (-> project-lib-deps
                     (into (mapcat #(brick-libs name->brick % :test)) all-brick-names)
                     (into (mapcat #(brick-libs name->brick % :src)) src-only-brick-names)
                     (lib/resolve-libs (merge override-src-deps override-test-deps))
                     set
                     sort
                     vec)]
    [lib-deps project-lib-deps]))

(defn skip-all-tests? [{:keys [include] :as test}]
  (or (vector? test)
      (and (-> include nil? not)
           (empty? include))))

(defn read-project
  ([{:keys [deps project-name project-dir project-config-dir is-dev]} ws-dir name->brick project->settings user-home suffixed-top-ns interface-ns]
   (let [{:keys [paths deps override-deps aliases mvn/repos mvn/local-repo]} deps
         project-src-paths (cond-> paths is-dev (concat (-> aliases :dev :extra-paths)))
         project-src-deps (cond-> deps is-dev (merge (-> aliases :dev :extra-deps)))
         project-test-paths (-> aliases :test :extra-paths)
         project-test-deps (-> aliases :test :extra-deps)
         entity-root-path (when (not is-dev) (str "projects/" project-name))
         override-src-deps (lib/latest-with-sizes ws-dir entity-root-path (if is-dev (-> aliases :dev :override-deps) override-deps) user-home)
         override-test-deps (lib/latest-with-sizes ws-dir entity-root-path (-> aliases :test :override-deps) user-home)
         maven-repos (merge mvn/standard-repos repos)
         src-paths (src-paths-from-bricks project-name is-dev name->brick project-src-paths project-src-deps)
         [src-lib-deps src-project-lib-deps] (src-lib-deps-from-bricks ws-dir project-name is-dev user-home name->brick project-src-deps override-src-deps)
         test (get-in project->settings [project-name :test])
         skip-all? (skip-all-tests? test)
         test-paths (if skip-all? [] (test-paths-from-bricks project-name is-dev name->brick project-test-paths project-src-deps project-test-deps))
         [test-lib-deps test-project-lib-deps] (if skip-all? [[][]] (test-lib-deps-from-bricks ws-dir project-name is-dev name->brick user-home project-src-deps project-test-deps override-src-deps override-test-deps))
         paths (cond-> {}
                       (seq src-paths) (assoc :src src-paths)
                       (seq test-paths) (assoc :test test-paths))
         lib-deps (cond-> {}
                          (seq src-lib-deps) (assoc :src src-lib-deps)
                          (seq test-lib-deps) (assoc :test test-lib-deps))
         project-lib-deps (cond-> {}
                                  (seq src-project-lib-deps) (assoc :src src-project-lib-deps)
                                  (seq test-project-lib-deps) (assoc :test test-project-lib-deps))
         {:keys [src-dirs test-dirs]} (project-paths/project-source-dirs ws-dir project-name is-dev project-src-paths project-test-paths)
         namespaces (ns-from-disk/namespaces-from-disk ws-dir src-dirs test-dirs suffixed-top-ns interface-ns)]
     (util/ordered-map :alias (get-in project->settings [project-name :alias])
                       :name project-name
                       :is-dev is-dev
                       :project-dir project-dir
                       :deps-filename (str project-config-dir "/deps.edn")
                       :type "project"
                       :paths paths
                       :lib-deps lib-deps
                       :project-lib-deps project-lib-deps
                       :maven-repos maven-repos
                       :maven-local-repo local-repo
                       :namespaces namespaces
                       :test test
                       :necessary (get-in project->settings [project-name :necessary])
                       :keep-lib-versions (get-in project->settings [project-name :keep-lib-versions])))))

(defn keep?
  "Skip projects that are passed in as e.g. skip:p1:p2."
  [{:keys [project-name config]} project->settings skip]
  (not (or (contains? skip project-name)
           (contains? skip (:alias config))
           (contains? skip (get-in project->settings [project-name :alias])))))

(defn read-projects [ws-dir name->brick project->settings user-input user-home suffixed-top-ns interface-ns configs]
  (let [skip (if user-input (-> user-input :skip set) #{})]
    (into []
          (comp (filter #(keep? % project->settings skip))
                (keep #(read-project % ws-dir name->brick project->settings user-home suffixed-top-ns interface-ns)))
          configs)))

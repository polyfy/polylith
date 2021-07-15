(ns polylith.clj.core.workspace-clj.projects-from-disk
  (:require [clojure.set :as set]
            [clojure.string :as str]
            [clojure.tools.deps.alpha.util.maven :as mvn]
            [polylith.clj.core.file.interface :as file]
            [polylith.clj.core.lib.interface :as lib]
            [polylith.clj.core.util.interface :as util]
            [polylith.clj.core.util.interface.str :as str-util]
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

(defn brick-name [path is-dev]
  (let [prefix (if is-dev "" "../../")
        base-path (str prefix "bases/")
        component-path (str prefix "components/")]
    (when path
      (cond
        (str/starts-with? path base-path) (str-util/skip-prefix path base-path)
        (str/starts-with? path component-path) (str-util/skip-prefix path component-path)))))

(defn brick? [[_ {:keys [local/root]}] is-dev]
  (and (-> root nil? not)
       (brick-path? root is-dev)))

(defn extract-brick-name
  "Returns the brick name from a dependency if it's a valid path to a brick."
  [[_ entry] is-dev]
  (let [path (:local/root entry)
        name (brick-name path is-dev)]
    (when name [name])))

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
  (let [brick-names (set (mapcat #(extract-brick-name % is-dev) project-src-deps))
        paths (vec (sort (set (concat (map #(absolute-path % project-name is-dev) project-src-paths)
                                      (mapcat #(-> % name->brick ->brick-src-paths) brick-names)))))
        entity-root-path (when (not is-dev) (str "projects/" project-name))
        lib-deps (lib/resolve-libs (concat (lib/with-sizes-vec ws-dir
                                                               entity-root-path
                                                               (filterv #(not (brick? % is-dev))
                                                                        project-src-deps)
                                                               user-home)
                                           (mapcat #(brick-libs name->brick % :src) brick-names))
                                   override-deps)]
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
  [ws-dir name->brick is-dev project-name bricks-to-test user-home project-src-deps project-test-deps project-test-paths override-src-deps override-test-deps]
  ;; todo: support filtering on individual bricks.
  (if (skip-all-tests? bricks-to-test)
    [[] []]
    (do
      (let [src-brick-names (set (mapcat #(extract-brick-name % is-dev) project-src-deps))
            test-brick-names (set (mapcat #(extract-brick-name % is-dev) project-test-deps))
            brick-names (set (mapcat #(extract-brick-name % is-dev)
                                     (concat project-src-deps project-test-deps)))
            only-brick-names (set/difference test-brick-names src-brick-names)
            paths (concat (map #(absolute-path % project-name is-dev) project-test-paths)
                          (mapcat #(-> % name->brick ->brick-test-paths) brick-names)
                          (mapcat #(-> % name->brick ->brick-src-paths) only-brick-names))
            entity-root-path (str "projects/" project-name)
            lib-deps (lib/resolve-libs (concat (lib/with-sizes-vec ws-dir
                                                                   entity-root-path
                                                                   (filterv #(not (brick? % is-dev))
                                                                            project-test-deps)
                                                                   user-home)
                                               (mapcat #(brick-libs name->brick % :test) brick-names)
                                               (mapcat #(brick-libs name->brick % :src) only-brick-names))
                                       (merge override-src-deps override-test-deps))]

        [(vec (sort (set paths)))
         (vec (sort (set lib-deps)))]))))

(comment
  (def src-deps [["clj-time/clj-time" {:version "0.15.2", :type "maven", :size 23664}]
                 ["clj-time/clj-time" {:version "0.15.0", :type "maven", :size 23664}]])

  (lib/with-sizes-vec nil src-deps "/Users/joakimtengstrand")
  #__)

(defn read-project
  ([{:keys [project-name project-dir project-config-dir is-dev]} ws-dir ws-type name->brick project->settings user-home]
   (let [config-filename (str project-config-dir "/deps.edn")
         {:keys [paths deps override-deps aliases mvn/repos] :as config} (read-string (slurp config-filename))
         project-src-paths (if is-dev (-> aliases :dev :extra-paths) paths)
         project-src-deps (if is-dev (-> aliases :dev :extra-deps) deps)
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
                     project-test-deps override-src-deps override-test-deps))))
  ([ws-dir name->brick project-name project-dir config-filename is-dev maven-repos
    project->settings user-home project-src-paths project-src-deps project-test-paths
    project-test-deps override-src-deps override-test-deps]
   (let [[src-paths src-lib-deps] (src-paths-and-libs-from-bricks ws-dir name->brick is-dev project-name user-home project-src-deps project-src-paths override-src-deps)
         bricks-to-test (-> project-name project->settings :test :include)
         [test-paths test-lib-deps] (test-paths-and-libs-from-bricks ws-dir name->brick is-dev project-name bricks-to-test user-home project-src-deps project-test-deps project-test-paths override-src-deps override-test-deps)
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

(defn read-projects [ws-dir ws-type name->brick project->settings user-input user-home]
  (let [skip (if user-input (-> user-input :skip set) #{})
        project-configs (filter #(keep? % project->settings skip)
                                (conj (map #(project-map ws-dir %)
                                           (file/directories (str ws-dir "/projects")))
                                      {:project-name "development"
                                       :is-dev true
                                       :project-dir (str ws-dir "/development")
                                       :project-config-dir ws-dir}))]
    (filterv identity
             (map #(read-project % ws-dir ws-type name->brick project->settings user-home) project-configs))))

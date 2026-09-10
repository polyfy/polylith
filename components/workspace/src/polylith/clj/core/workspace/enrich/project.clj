(ns ^:no-doc polylith.clj.core.workspace.enrich.project
  (:require [clojure.string :as str]
            [polylith.clj.core.common.interface :as common]
            [polylith.clj.core.deps.interface :as deps]
            [polylith.clj.core.file.interface :as file]
            [polylith.clj.core.lib.interface :as lib]
            [polylith.clj.core.path-finder.interface.select :as select]
            [polylith.clj.core.path-finder.interface.extract :as extract]
            [polylith.clj.core.path-finder.interface.criterias :as c]
            [polylith.clj.core.workspace.enrich.loc :as loc]
            [polylith.clj.core.workspace.enrich.project-test-settings :as test-settings]))

(defn project-total-loc [brick-names brick->loc]
  {:src (apply + (filter identity (map #(-> % brick->loc :src) brick-names)))
   :test (apply + (filter identity (map #(-> % brick->loc :test) brick-names)))})

(defn source-imports [brick-names brick->lib-imports source-key]
  (-> (mapcat #(-> % brick->lib-imports source-key) brick-names)
      set sort vec))

(defn project-lib-imports [brick-names brick->lib-imports]
  (let [src (source-imports brick-names brick->lib-imports :src)
        test (source-imports brick-names brick->lib-imports :test)]
    (cond-> {}
            (seq src) (assoc :src src)
            (seq test) (assoc :test test))))

(defn project-alias! [alias alias-id dev?]
  (or alias
      (if dev?
        "dev"
        (str "?" (swap! alias-id inc)))))

(defn enrich-project [{:keys [alias name type is-dev test maven-repos namespaces paths lib-deps project-lib-deps] :as project}
                      ws-dir
                      alias-id
                      components
                      bases
                      profiles
                      suffixed-top-ns
                      brick->loc
                      brick->lib-imports
                      disk-paths
                      user-input
                      settings
                      name-type->keep-lib-version
                      outdated-libs
                      library->latest-version]
  (let [alias (project-alias! alias alias-id is-dev)
        path-entries (extract/from-unenriched-project is-dev paths disk-paths profiles settings)
        component-names-src (select/names path-entries c/component? c/src? c/exists?)
        component-names-test (select/names path-entries c/component? c/test? c/exists?)
        base-names-src (select/names path-entries c/base? c/src? c/exists?)
        base-names-test (select/names path-entries c/base? c/test? c/exists?)
        lib-entries (extract/from-library-deps is-dev lib-deps profiles settings)
        lib-deps-src (select/lib-deps lib-entries c/src?)
        lib-deps-test (select/lib-deps lib-entries c/test?)
        all-brick-names (concat component-names-src base-names-src component-names-test base-names-test)
        brick-names-to-test (common/brick-names-to-test test all-brick-names)
        lib-imports (project-lib-imports all-brick-names brick->lib-imports)
        lines-of-code-total (project-total-loc all-brick-names brick->loc)
        lines-of-code (assoc (loc/lines-of-code ws-dir namespaces) :total lines-of-code-total)
        deps (deps/project-deps components bases component-names-src component-names-test base-names-src base-names-test suffixed-top-ns brick-names-to-test)
        base-names (cond-> {}
                           (seq base-names-src) (assoc :src base-names-src)
                           (seq base-names-test) (assoc :test base-names-test))
        component-names (cond-> {}
                                (seq component-names-src) (assoc :src component-names-src)
                                (seq component-names-test) (assoc :test component-names-test))
        project-lib-entries (extract/from-library-deps is-dev project-lib-deps profiles settings)
        project-lib-deps (lib/lib-deps-with-latest-version name
                                                           type
                                                           {:src (select/lib-deps project-lib-entries c/src?)
                                                            :test (select/lib-deps project-lib-entries c/test?)}
                                                           outdated-libs
                                                           library->latest-version
                                                           user-input
                                                           name-type->keep-lib-version)
        src-paths (select/paths path-entries c/src?)
        test-paths (select/paths path-entries c/test?)
        source-paths (cond-> {}
                             (seq src-paths) (assoc :src src-paths)
                             (seq test-paths) (assoc :test test-paths))
        source-lib-deps (cond-> {}
                                (seq lib-deps-src) (assoc :src lib-deps-src)
                                (seq lib-deps-test) (assoc :test lib-deps-test))
        enriched-maven-repos (apply merge maven-repos (mapcat :maven-repos (concat components bases)))]
    (-> project
        (merge {:alias alias
                :lines-of-code lines-of-code
                :component-names component-names
                :base-names base-names
                :deps deps
                :paths source-paths
                :lib-deps source-lib-deps
                :test (test-settings/enrich test settings)
                :project-lib-deps project-lib-deps
                :lib-imports lib-imports})
        (cond-> enriched-maven-repos (assoc :maven-repos enriched-maven-repos)
                is-dev (assoc :unmerged {:paths paths
                                         :lib-deps lib-deps})))))

(defn- with-absolute-local-roots
  "'tools.deps' resolves ':local/root' relative to the current directory, but poly
   stores local-library paths relative to the workspace root. Rewrite them to
   absolute paths so the dependency tree resolves regardless of where 'poly' was
   invoked from (e.g. 'poly libs :transitive ws-dir:some/other/ws')."
  [ws-dir lib-deps]
  (let [abs-dir (str/replace (file/absolute-path ws-dir) #"/$" "")
        fix (fn [entries]
              (into {} (map (fn [[name {:keys [type path] :as coords}]]
                              [name (if (and (= "local" type)
                                             path
                                             (not (str/starts-with? path "/")))
                                      (assoc coords :path (str abs-dir "/" path))
                                      coords)])
                            entries)))]
    (cond-> lib-deps
            (:src lib-deps) (update :src fix)
            (:test lib-deps) (update :test fix))))

(defn with-indirect-lib-deps
  "Resolves the project's full dependency tree and attaches ':indirect-lib-deps' -
   the resolved Maven libraries that are on the classpath but not already in the
   project's declared ':lib-deps' (i.e. pulled in transitively, possibly via a
   ':local/root' library). Same value shape as ':lib-deps' entries.
   Only used when ':transitive' is passed (e.g. 'poly libs :transitive').

   'declared-libs' is a '{[lib-name version] size-or-nil}' map built from the
   libraries that bricks/projects already declare. A resolved library that also
   appears there is given the declared side's size, so both representations are
   identical - and thus a single row. Resolving downloads jars as a side effect,
   so measuring the size here directly would give a jar that the declared side
   (measured earlier) may not yet have had. Purely-transitive libraries keep the
   freshly measured size."
  [ws-dir {:keys [lib-deps] :as project} {:keys [user-home] :as settings} declared-libs]
  (let [declared (set (concat (keys (:src lib-deps))
                              (keys (:test lib-deps))))
        project (update project :lib-deps #(with-absolute-local-roots ws-dir %))
        resolved (try
                   (deps/resolve-deps project settings false)
                   (catch Exception _ nil))
        entries (keep (fn [[lib {:keys [mvn/version]}]]
                        (when (and version (not (contains? declared (str lib))))
                          [(str lib) {:mvn/version version}]))
                      resolved)
        indirect (into {}
                       (map (fn [[name coords]]
                              (let [k [name (:version coords)]]
                                [name (if (contains? declared-libs k)
                                        (cond-> (dissoc coords :size)
                                                (declared-libs k) (assoc :size (declared-libs k)))
                                        coords)])))
                       (lib/with-sizes-vec ws-dir nil entries user-home))]
    (cond-> project
            (seq indirect) (assoc :indirect-lib-deps {:src indirect}))))

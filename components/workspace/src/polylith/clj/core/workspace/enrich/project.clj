(ns ^:no-doc polylith.clj.core.workspace.enrich.project
  (:require [polylith.clj.core.common.interface :as common]
            [polylith.clj.core.deps.interface :as deps]
            [polylith.clj.core.file.interface :as file]
            [polylith.clj.core.lib.interface :as lib]
            [polylith.clj.core.path-finder.interface.select :as select]
            [polylith.clj.core.path-finder.interface.extract :as extract]
            [polylith.clj.core.path-finder.interface.criterias :as c]
            [polylith.clj.core.workspace.enrich.project-ws-brick :as ws-brick]
            [polylith.clj.core.workspace.enrich.loc :as loc]
            [polylith.clj.core.workspace.enrich.project-test-settings :as test-settings]))

(defn file-exists [ws-dir cleaned-path]
  (file/exists (str ws-dir "/" cleaned-path)))

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
                      configs
                      settings
                      name-type->keep-lib-version
                      outdated-libs
                      library->latest-version]
  (let [path-entries (extract/from-unenriched-project is-dev paths disk-paths profiles settings)
        component-names-src (select/names path-entries c/component? c/src? c/exists?)
        component-names-test (select/names path-entries c/component? c/test? c/exists?)
        base-names-src (select/names path-entries c/base? c/src? c/exists?)
        base-names-test (select/names path-entries c/base? c/test? c/exists?)
        all-brick-names (concat component-names-src base-names-src component-names-test base-names-test)
        brick-names-to-test (common/brick-names-to-test test all-brick-names)
        deps (deps/project-deps components bases component-names-src component-names-test base-names-src base-names-test suffixed-top-ns brick-names-to-test)
        lib-imports (project-lib-imports all-brick-names brick->lib-imports)
        lines-of-code-total (project-total-loc all-brick-names brick->loc)
        lines-of-code (assoc (loc/lines-of-code ws-dir namespaces) :total lines-of-code-total)
        lib-entries (extract/from-library-deps is-dev lib-deps profiles settings)
        lib-deps-src (select/lib-deps lib-entries c/src?)
        [base-names-src-x component-names-src-x lib-deps-with-ws-bricks-src] (ws-brick/convert-libs-to-bricks lib-deps-src configs)
        base-names-src (vec (concat base-names-src base-names-src-x))
        component-names-src (vec (concat component-names-src component-names-src-x))
        lib-deps-test (select/lib-deps lib-entries c/test?)
        [base-names-test-x component-names-test-x lib-deps-with-ws-bricks-test] (ws-brick/convert-libs-to-bricks lib-deps-test configs)
        base-names-test (vec (concat base-names-test base-names-test-x))
        component-names-test (vec (concat component-names-test component-names-test-x))
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
                                (seq lib-deps-with-ws-bricks-src) (assoc :src lib-deps-with-ws-bricks-src)
                                (seq lib-deps-with-ws-bricks-test) (assoc :test lib-deps-with-ws-bricks-test))
        enriched-maven-repos (apply merge maven-repos (mapcat :maven-repos (concat components bases)))]
    (-> project
        (merge {:alias (project-alias! alias alias-id is-dev)
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

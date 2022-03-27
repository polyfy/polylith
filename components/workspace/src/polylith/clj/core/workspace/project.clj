(ns polylith.clj.core.workspace.project
  (:require [polylith.clj.core.deps.interface :as proj-deps]
            [polylith.clj.core.file.interface :as file]
            [polylith.clj.core.path-finder.interface.select :as select]
            [polylith.clj.core.path-finder.interface.extract :as extract]
            [polylith.clj.core.path-finder.interface.criterias :as c]
            [polylith.clj.core.workspace.loc :as loc]))

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

(defn enrich-project [{:keys [name is-dev maven-repos namespaces paths lib-deps] :as project}
                      components
                      bases
                      suffixed-top-ns
                      brick->loc
                      brick->lib-imports
                      disk-paths
                      settings
                      {:keys [is-run-all-brick-tests selected-projects] :as user-input}]
  (let [is-dev-user-input (:is-dev user-input)
        alias (get-in settings [:projects name :alias])
        enriched-maven-repos (apply merge maven-repos (mapcat :maven-repos (concat components bases)))
        lib-entries (extract/from-library-deps is-dev lib-deps settings)
        path-entries (extract/from-unenriched-project is-dev paths disk-paths settings)
        component-names-src (select/names path-entries c/component? c/src? c/exists?)
        component-names-test (select/names path-entries c/component? c/test? c/exists?)
        component-names (cond-> {}
                                (seq component-names-src) (assoc :src component-names-src)
                                (seq component-names-test) (assoc :test component-names-test))
        base-names-src (select/names path-entries c/base? c/src? c/exists?)
        base-names-test (select/names path-entries c/base? c/test? c/exists?)
        base-names (cond-> {}
                           (seq base-names-src) (assoc :src base-names-src)
                           (seq base-names-test) (assoc :test base-names-test))
        all-brick-names (concat component-names-src base-names-src component-names-test base-names-test)
        ;; todo: maybe we can remove the 'bricks-to-test' check from here, because the tests are eliminated in 'workspace-clj' already.
        bricks-to-test (when-let [bricks (get-in settings [:projects name :test :include])] (set bricks))
        deps (proj-deps/project-deps components bases component-names-src component-names-test base-names-src base-names-test suffixed-top-ns bricks-to-test)

        lib-imports (project-lib-imports all-brick-names brick->lib-imports)

        lines-of-code-total (project-total-loc all-brick-names brick->loc)
        lines-of-code (assoc (loc/lines-of-code namespaces) :total lines-of-code-total)
        src-lib-deps (select/lib-deps lib-entries c/src?)
        test-lib-deps (select/lib-deps lib-entries c/test?)
        src-paths (select/paths path-entries c/src?)
        test-paths (select/paths path-entries c/test?)
        merged-paths (cond-> {}
                             (seq src-paths) (assoc :src src-paths)
                             (seq test-paths) (assoc :test test-paths))
        merged-lib-deps (cond-> {}
                                (seq src-lib-deps) (assoc :src src-lib-deps)
                                (seq test-lib-deps) (assoc :test test-lib-deps))]
    (-> project
        (merge {:alias            alias
                :lines-of-code    lines-of-code
                :component-names  component-names
                :base-names       base-names
                :deps             deps
                :paths            merged-paths
                :lib-deps         merged-lib-deps
                :lib-imports      lib-imports})
        (cond-> enriched-maven-repos (assoc :maven-repos  enriched-maven-repos)
                is-dev (assoc :unmerged {:paths     paths
                                         :lib-deps      lib-deps})))))

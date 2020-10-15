(ns polylith.clj.core.workspace.project
  (:require [polylith.clj.core.file.interface :as file]
            [polylith.clj.core.path-finder.interface.select :as select]
            [polylith.clj.core.path-finder.interface.extract :as extract]
            [polylith.clj.core.path-finder.interface.criterias :as c]
            [polylith.clj.core.workspace.brick-deps :as brick-deps]
            [polylith.clj.core.workspace.loc :as loc]))

(defn file-exists [ws-dir cleaned-path]
  (file/exists (str ws-dir "/" cleaned-path)))

(defn project-total-loc [brick-names brick->loc test?]
  (let [locs (map brick->loc brick-names)]
    (if test?
      (apply + (filter identity (map :lines-of-code-test locs)))
      (apply + (filter identity (map :lines-of-code-src locs))))))

(defn select-lib-imports [brick-name brick->lib-imports test?]
  (let [{:keys [lib-imports-src lib-imports-test]} (brick->lib-imports brick-name)]
    (if test?
      lib-imports-test
      lib-imports-src)))

(defn project-lib-imports [brick-names brick->lib-imports test?]
  (-> (mapcat #(select-lib-imports % brick->lib-imports test?)
              brick-names)
      set sort vec))

(defn run-the-tests? [projt-name alias is-dev is-run-all-brick-tests selected-projects]
  (or (and (not is-dev)
           (or is-run-all-brick-tests
               (empty? selected-projects)))
      (or (contains? selected-projects projt-name)
          (contains? selected-projects alias))))

(defn enrich-project [{:keys [name is-dev namespaces-src namespaces-test src-paths test-paths lib-deps test-lib-deps] :as project}
                      components
                      bases
                      brick->loc
                      brick->lib-imports
                      project-to-alias
                      disk-paths
                      settings
                      {:keys [is-run-all-brick-tests selected-projects]}]
  (let [alias (project-to-alias name)
        lib-entries (extract/from-library-deps is-dev lib-deps test-lib-deps settings)
        path-entries (extract/from-unenriched-project is-dev src-paths test-paths disk-paths settings)
        component-names (select/names path-entries c/component? c/src? c/exists?)
        base-names (select/names path-entries c/base? c/src? c/exists?)
        brick-names (concat component-names base-names)
        test-component-names (select/names path-entries c/component? c/test? c/exists?)
        test-base-names (select/names path-entries c/base? c/test? c/exists?)
        deps (brick-deps/project-deps component-names base-names components bases)
        lib-imports-src (project-lib-imports brick-names brick->lib-imports false)
        lib-imports-test (project-lib-imports brick-names brick->lib-imports true)
        is-run-tests (run-the-tests? name alias is-dev is-run-all-brick-tests selected-projects)
        total-lines-of-code-src (project-total-loc brick-names brick->loc false)
        total-lines-of-code-test (project-total-loc brick-names brick->loc true)]
    (-> project
        (merge {:alias                    alias
                :is-run-tests             is-run-tests
                :lines-of-code-src        (loc/lines-of-code namespaces-src)
                :lines-of-code-test       (loc/lines-of-code namespaces-test)
                :total-lines-of-code-src  total-lines-of-code-src
                :total-lines-of-code-test total-lines-of-code-test
                :test-component-names     test-component-names
                :component-names          (select/names path-entries c/component? c/src? c/exists?)
                :base-names               base-names
                :test-base-names          test-base-names
                :src-paths                (select/paths path-entries c/src?)
                :test-paths               (select/paths path-entries c/test?)
                :lib-deps                 (select/lib-deps lib-entries c/src?)
                :test-lib-deps            (select/lib-deps lib-entries c/test?)
                :unmerged                 (when is-dev {:src-paths     src-paths
                                                        :test-paths    test-paths
                                                        :lib-deps      lib-deps
                                                        :test-lib-deps test-lib-deps})
                :lib-imports              lib-imports-src
                :lib-imports-test         lib-imports-test
                :deps                     deps})
        (cond-> is-dev (assoc :unmerged {:src-paths     src-paths
                                         :test-paths    test-paths
                                         :lib-deps      lib-deps
                                         :test-lib-deps test-lib-deps})))))

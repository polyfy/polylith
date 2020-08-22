(ns polylith.clj.core.workspace.environment
  (:require [polylith.clj.core.file.interfc :as file]
            [polylith.clj.core.util.interfc :as util]
            [polylith.clj.core.path-finder.interfc.select :as select]
            [polylith.clj.core.path-finder.interfc.extract :as extract]
            [polylith.clj.core.path-finder.interfc.criterias :as c]
            [polylith.clj.core.workspace.loc :as loc]
            [polylith.clj.core.workspace.brick-deps :as brick-deps]))

(defn file-exists [ws-dir cleaned-path]
  (file/exists (str ws-dir "/" cleaned-path)))

(defn env-total-loc [brick-names brick->loc test?]
  (let [locs (map brick->loc brick-names)]
    (if test?
      (apply + (filter identity (map :lines-of-code-test locs)))
      (apply + (filter identity (map :lines-of-code-src locs))))))

(defn select-lib-imports [brick-name brick->lib-imports test?]
  (let [{:keys [lib-imports-src lib-imports-test]} (brick->lib-imports brick-name)]
    (if test?
      lib-imports-test
      lib-imports-src)))

(defn env-lib-imports [brick-names brick->lib-imports test?]
  (mapcat #(select-lib-imports % brick->lib-imports test?)
          brick-names))

(defn active? [env alias dev? run-all? selected-environments]
  (or (and (not dev?)
           (or run-all?
               (empty? selected-environments)))
      (or (contains? selected-environments env)
          (contains? selected-environments alias))))

(defn enrich-env [{:keys [name type dev? env-dir config-file namespaces-src namespaces-test src-paths test-paths lib-deps test-lib-deps maven-repos]}
                  ws-dir
                  components
                  bases
                  brick->loc
                  brick->lib-imports
                  env->alias
                  settings
                  {:keys [run-all? selected-environments] :as user-input}]
  (let [alias (env->alias name)
        dep-entries (extract/from-library-deps dev? lib-deps test-lib-deps settings user-input)
        path-entries (extract/from-unenriched-environment ws-dir dev? src-paths test-paths settings user-input)
        component-names (select/names path-entries c/component? c/src? c/exists?)
        base-names (select/names path-entries c/base? c/src? c/exists?)
        brick-names (concat component-names base-names)
        test-component-names (select/names path-entries c/component? c/test? c/exists?)
        test-base-names (select/names path-entries c/base? c/test? c/exists?)
        deps (brick-deps/environment-deps component-names components bases)
        lib-imports-src (-> (env-lib-imports brick-names brick->lib-imports false)
                            set sort vec)
        lib-imports-test (-> (env-lib-imports brick-names brick->lib-imports true)
                             set sort vec)
        total-lines-of-code-src (env-total-loc brick-names brick->loc false)
        total-lines-of-code-test (env-total-loc brick-names brick->loc true)]
    (util/ordered-map :name name
                      :alias alias
                      :type type
                      :active? (active? name alias dev? run-all? selected-environments)
                      :dev? dev?
                      :env-dir env-dir
                      :config-file config-file
                      :lines-of-code-src (loc/lines-of-code namespaces-src)
                      :lines-of-code-test (loc/lines-of-code namespaces-test)
                      :total-lines-of-code-src total-lines-of-code-src
                      :total-lines-of-code-test total-lines-of-code-test
                      :test-component-names test-component-names
                      :component-names (select/names path-entries c/component? c/src? c/exists?)
                      :base-names base-names
                      :test-base-names test-base-names
                      :namespaces-src namespaces-src
                      :namespaces-test namespaces-test
                      :src-paths src-paths
                      :test-paths test-paths
                      :profile-src-paths (select/paths path-entries c/profile? c/src?)
                      :profile-test-paths (select/paths path-entries c/profile? c/test?)
                      :missing-paths (select/paths path-entries c/not-exists? c/not-test-or-resources-path)
                      :lib-imports lib-imports-src
                      :lib-imports-test lib-imports-test
                      :lib-deps (select/lib-deps dep-entries c/src?)
                      :deps deps
                      :test-lib-deps (select/lib-deps dep-entries c/test?)
                      :maven-repos maven-repos)))

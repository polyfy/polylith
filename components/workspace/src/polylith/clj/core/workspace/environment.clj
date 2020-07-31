(ns polylith.clj.core.workspace.environment
  (:require [polylith.clj.core.util.interfc :as util]
            [polylith.clj.core.workspace.loc :as loc]
            [clojure.string :as str]))

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

(defn enrich-env [{:keys [name type env-dir config-file component-names test-component-names base-names test-base-names has-src-dir? has-test-dir? namespaces-src namespaces-test paths test-paths lib-deps test-deps maven-repos]}
                  brick->loc
                  brick->lib-imports
                  env->alias
                  env->brick-deps]
  (let [brick-names (concat component-names base-names)
        lib-imports-src (-> (env-lib-imports brick-names brick->lib-imports false)
                            set sort vec)
        lib-imports-test (-> (env-lib-imports brick-names brick->lib-imports true)
                             set sort vec)
        total-lines-of-code-src (env-total-loc brick-names brick->loc false)
        total-lines-of-code-test (env-total-loc brick-names brick->loc true)]
    (util/ordered-map :name name
                      :alias (env->alias name)
                      :type type
                      :env-dir env-dir
                      :config-file config-file
                      :lines-of-code-src (loc/lines-of-code namespaces-src)
                      :lines-of-code-test (loc/lines-of-code namespaces-test)
                      :total-lines-of-code-src total-lines-of-code-src
                      :total-lines-of-code-test total-lines-of-code-test
                      :test-component-names test-component-names
                      :component-names component-names
                      :base-names base-names
                      :test-base-names test-base-names
                      :has-src-dir? has-src-dir?
                      :has-test-dir? has-test-dir?
                      :namespaces-src namespaces-src
                      :namespaces-test namespaces-test
                      :paths paths
                      :test-paths test-paths
                      :lib-imports lib-imports-src
                      :lib-imports-test lib-imports-test
                      :lib-deps lib-deps
                      :deps (env->brick-deps name)
                      :test-deps test-deps
                      :maven-repos maven-repos)))

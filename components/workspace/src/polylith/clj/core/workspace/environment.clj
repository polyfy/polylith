(ns polylith.clj.core.workspace.environment
  (:require [clojure.string :as str]
            [polylith.clj.core.file.interfc :as file]
            [polylith.clj.core.util.interfc :as util]
            [polylith.clj.core.workspace.profile :as profile]
            [polylith.clj.core.workspace.loc :as loc]))

(defn starts-with [path start]
  (and (string? path)
       (str/starts-with? path start)))

(defn component? [path]
  (starts-with path "components/"))

(defn base? [path]
  (starts-with path "bases/"))

(defn brick-name [path start-index]
  (let [end-index (+ start-index (str/index-of (subs path start-index) "/"))]
    (if (< end-index 0)
      path
      (subs path start-index end-index))))

(defn component-name [path]
  (brick-name path 11))

(defn base-name [path]
  (brick-name path 6))

(defn file-exists [ws-dir cleaned-path]
  (file/exists (str ws-dir "/" cleaned-path)))

(defn existing-paths [ws-dir paths]
  (filterv #(file-exists ws-dir %) paths))

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

(defn enrich-env [{:keys [name type env-dir config-file has-src-dir? has-test-dir? namespaces-src namespaces-test src-paths test-paths lib-deps test-deps maven-repos]}
                  ws-dir
                  brick->loc
                  brick->lib-imports
                  env->alias
                  env->brick-deps
                  active-dev-profiles
                  profile->settings]
  (let [all-src-paths (profile/src-paths name src-paths active-dev-profiles profile->settings)
        all-test-paths (profile/test-paths name test-paths active-dev-profiles profile->settings)
        existing-src-paths (existing-paths ws-dir all-src-paths)
        component-names (vec (sort (set (mapv component-name (filter component? existing-src-paths)))))
        base-names (vec (sort (set (mapv base-name (filter base? existing-src-paths)))))
        brick-names (concat component-names base-names)
        existing-test-paths (existing-paths ws-dir all-test-paths)
        test-component-names (vec (sort (set (mapv component-name (filter component? existing-test-paths)))))
        test-base-names (vec (sort (set (mapv base-name (filter base? existing-test-paths)))))
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
                      :src-paths all-src-paths
                      :test-paths all-test-paths
                      :lib-imports lib-imports-src
                      :lib-imports-test lib-imports-test
                      :lib-deps (profile/lib-deps name lib-deps active-dev-profiles profile->settings)
                      :deps (env->brick-deps name)
                      :test-deps test-deps
                      :maven-repos maven-repos)))

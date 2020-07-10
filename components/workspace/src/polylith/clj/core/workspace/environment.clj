(ns polylith.clj.core.workspace.environment
  (:require [polylith.clj.core.util.interfc :as util]
            [clojure.string :as str]))

(defn env-loc [brick-names brick->loc test?]
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

(defn ws-root-path [path env]
  (cond
    (str/starts-with? path "./") (str "environments/" env "/" (subs path 2))
    (str/starts-with? path "../../") (subs path 6)
    :else (str "environments/" env "/" path)))

(defn ws-root-paths [paths env]
  (mapv #(ws-root-path % env) paths))

(defn enrich-env [{:keys [name type component-names test-component-names base-names test-base-names paths test-paths deps test-deps maven-repos]}
                  brick->loc
                  brick->lib-imports
                  env->alias]
  (let [brick-names (concat component-names base-names)
        lib-imports-src (-> (env-lib-imports brick-names brick->lib-imports false)
                            set sort vec)
        lib-imports-test (-> (env-lib-imports brick-names brick->lib-imports true)
                             set sort vec)
        lines-of-code-src (env-loc brick-names brick->loc false)
        lines-of-code-test (env-loc brick-names brick->loc true)]
    (util/ordered-map :name name
                      :alias (env->alias name)
                      :type type
                      :lines-of-code-src lines-of-code-src
                      :lines-of-code-test lines-of-code-test
                      :test-component-names test-component-names
                      :component-names component-names
                      :base-names base-names
                      :test-base-names test-base-names
                      :paths (ws-root-paths paths name)
                      :test-paths (ws-root-paths test-paths name)
                      :lib-imports lib-imports-src
                      :lib-imports-test lib-imports-test
                      :deps deps
                      :test-deps test-deps
                      :maven-repos maven-repos)))

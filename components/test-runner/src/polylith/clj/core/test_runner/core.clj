(ns polylith.clj.core.test-runner.core
  (:require [clojure.string :as str]
            [clojure.tools.deps.alpha :as tools-deps]
            [polylith.clj.core.common.interface :as common]
            [polylith.clj.core.util.interface.color :as color]
            [polylith.clj.core.util.interface.str :as str-util]
            [polylith.clj.core.util.interface.time :as time-util])
  (:refer-clojure :exclude [test]))

(defn key-as-symbol [[library version]]
  "The library names (keys) are stored as strings in the workspace
   and need to be converted back to symbols here."
  [(symbol library) version])

(defn ->config [workspace {:keys [lib-deps test-lib-deps maven-repos]}]
  (assoc workspace :mvn/repos maven-repos
                   :deps (into {} (map key-as-symbol (merge lib-deps test-lib-deps)))))

(defn ->test-statement [ns-name]
  (let [ns-symbol (symbol ns-name)]
    `(do (use 'clojure.test)
         (require '~ns-symbol)
         (clojure.test/run-tests '~ns-symbol))))

(defn resolve-deps [{:keys [deps] :as config}]
  (try
    (into #{} (mapcat #(-> % second :paths)
                      (tools-deps/resolve-deps config {:extra-deps deps})))
    (catch Exception e
      (println e)
      (throw e))))

(defn ->test-namespaces [bricks test-brick-names]
  (let [brick-name->namespaces (into {} (map (juxt :name :namespaces-test) bricks))]
    (mapv :namespace (mapcat brick-name->namespaces test-brick-names))))

(defn environment-test-namespaces [env environments-to-test namespaces-test]
  (when (contains? (set environments-to-test) env)
    (map :namespace namespaces-test)))

(defn run-tests-statements [class-loader test-statements run-message color-mode]
  (println (str run-message))
  (doseq [statement test-statements]
    (let [{:keys [error fail pass] :as summary}
          (try
            (common/eval-in class-loader statement)
            (catch Exception e
              (.printStackTrace e)
              (println (str (color/error color-mode "Couldn't run test statement: ") statement " " (color/error color-mode e)))))
          result-str (str "Test results: " pass " passes, " fail " failures, " error " errors.")]
      (when (or (nil? error)
                (< 0 error)
                (< 0 fail))
        (throw (Exception. (str "\n" (color/error color-mode result-str)) summary)))
      (println (str "\n" (color/ok color-mode result-str))))))

(defn run-message [env components bases bricks-to-test-for-env environments-to-test color-mode]
  (let [component-names (set (map :name components))
        base-names (set (map :name bases))
        bases-to-test (filter #(contains? base-names %) bricks-to-test-for-env)
        bases-to-test-msg (when (-> bases-to-test empty? not) [(color/base (str/join ", " bases-to-test) color-mode)])
        components-to-test (filter #(contains? component-names %) bricks-to-test-for-env)
        components-to-test-msg (when (-> components-to-test empty? not) [(color/component (str/join ", " components-to-test) color-mode)])
        environments-to-test-msg (when (-> environments-to-test empty? not) [(color/environment (str/join ", " environments-to-test) color-mode)])
        entities-msg (str/join ", " (concat components-to-test-msg
                                            bases-to-test-msg
                                            environments-to-test-msg))
        env-cnt (count environments-to-test)
        bricks-cnt (count bricks-to-test-for-env)
        env-msg (if (zero? env-cnt)
                  ""
                  (str " and " (str-util/count-things "environment" env-cnt)))]
    (str "Runing tests for the " (color/environment env color-mode) " environment, including "
         (str-util/count-things "brick" bricks-cnt) env-msg ": " entities-msg)))

(defn run-tests-for-environment [{:keys [bases components] :as workspace}
                                 {:keys [name src-paths test-paths profile-src-paths profile-test-paths namespaces-test] :as environment}
                                 {:keys [env->bricks-to-test environments-to-test]}]
  (when (-> test-paths empty? not)
    (let [color-mode (-> workspace :settings :color-mode)
          config (->config workspace environment)
          lib-paths (resolve-deps config)
          all-src-paths (set (concat src-paths test-paths profile-src-paths profile-test-paths))
          all-paths (concat all-src-paths lib-paths)
          bricks (concat components bases)
          bricks-to-test-for-env (env->bricks-to-test name)
          run-message (run-message name components bases bricks-to-test-for-env environments-to-test color-mode)
          test-namespaces (->test-namespaces bricks bricks-to-test-for-env)
          env-test-namespaces (environment-test-namespaces name environments-to-test namespaces-test)
          test-statements (map ->test-statement (concat test-namespaces env-test-namespaces))
          class-loader (common/create-class-loader all-paths color-mode)]
      (if (-> test-statements empty?)
        (println (str "No tests to run for the " (color/environment name color-mode) " environment."))
        (run-tests-statements class-loader test-statements run-message color-mode)))))

(defn run [{:keys [environments changes] :as workspace}]
  (let [start-time (time-util/current-time)
        environments-to-test (filter :run-tests? environments)]
    (doseq [environment environments-to-test]
      (run-tests-for-environment workspace environment changes))
    (when (empty? environments-to-test)
      (println "  No tests to run. To run tests for 'dev', also give: env:dev"))
    (time-util/print-execution-time start-time)))

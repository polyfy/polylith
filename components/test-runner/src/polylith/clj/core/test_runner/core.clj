(ns polylith.clj.core.test-runner.core
  (:require [clojure.string :as str]
            [clojure.tools.deps.alpha :as tools-deps]
            [polylith.clj.core.common.interface :as common]
            [polylith.clj.core.util.interface.color :as color]
            [polylith.clj.core.util.interface.str :as str-util]
            [polylith.clj.core.util.interface.time :as time-util]
            [polylith.clj.core.validator.interface :as validator])
  (:refer-clojure :exclude [test]))

(defn adjust-key [{:keys [type path version]}]
  (case type
    "maven" {:mvn/version version}
    "local" {:local/root path}
    (throw (Exception. (str "Unknown library type: " type)))))

(defn key-as-symbol [[library version]]
  "The library names (keys) are stored as strings in the workspace
   and need to be converted back to symbols here.
   Library dependencies are stored as :type and :version and needs
   to be translated back to :mvn/version and :local/root."
  [(symbol library) (adjust-key version)])

(defn ->config [workspace {:keys [lib-deps test-lib-deps maven-repos]}]
  (assoc workspace :mvn/repos maven-repos
                   :deps (into {} (map key-as-symbol (merge lib-deps test-lib-deps)))))

(defn ->test-statement [ns-name]
  (let [ns-symbol (symbol ns-name)]
    `(do (use 'clojure.test)
         (require '~ns-symbol)
         (clojure.test/run-tests '~ns-symbol))))

(defn resolve-deps [env {:keys [deps] :as config} color-mode]
  (try
    (into #{} (mapcat #(-> % second :paths)
                      (tools-deps/resolve-deps config {:extra-deps deps})))
    (catch Exception e
      (println (str "Couldn't resolve libraries for the " (color/environment env color-mode) " environment: " e))
      (throw e))))

(defn brick-test-namespaces [bricks test-brick-names]
  (let [brick-name->namespaces (into {} (map (juxt :name :namespaces-test) bricks))]
    (mapv :namespace (mapcat brick-name->namespaces test-brick-names))))

(defn environment-test-namespaces [env environments-to-test namespaces-test]
  (when (contains? (set environments-to-test) env)
    (map :namespace namespaces-test)))

(defn run-test-statements [env class-loader test-statements run-message color-mode]
  (println (str run-message))
  (doseq [statement test-statements]
    (let [{:keys [error fail pass]}
          (try
            (common/eval-in class-loader statement)
            (catch Exception e
              (.printStackTrace e)
              (println (str (color/error color-mode "Couldn't run test statement") " for the " (color/environment env color-mode)  " environment: " statement " " (color/error color-mode e)))))
          result-str (str "Test results: " pass " passes, " fail " failures, " error " errors.")]
      (when (or (nil? error)
                (< 0 error)
                (< 0 fail))
        (throw (Exception. (str "\n" (color/error color-mode result-str)))))
      (println (str "\n" (color/ok color-mode result-str))))))

(defn run-message [env components bases bricks-to-test environments-to-test color-mode]
  (let [component-names (set (map :name components))
        base-names (set (map :name bases))
        bases-to-test (filter #(contains? base-names %) bricks-to-test)
        bases-to-test-msg (when (-> bases-to-test empty? not) [(color/base (str/join ", " bases-to-test) color-mode)])
        components-to-test (filter #(contains? component-names %) bricks-to-test)
        components-to-test-msg (when (-> components-to-test empty? not) [(color/component (str/join ", " components-to-test) color-mode)])
        environments-to-test-msg (when (-> environments-to-test empty? not) [(color/environment (str/join ", " environments-to-test) color-mode)])
        entities-msg (str/join ", " (concat components-to-test-msg
                                            bases-to-test-msg
                                            environments-to-test-msg))
        env-cnt (count environments-to-test)
        bricks-cnt (count bricks-to-test)
        env-msg (if (zero? env-cnt)
                  ""
                  (str " and " (str-util/count-things "environment" env-cnt)))]
    (str "Running tests from the " (color/environment env color-mode) " environment, including "
         (str-util/count-things "brick" bricks-cnt) env-msg ": " entities-msg)))

(defn run-tests-for-environment [{:keys [bases components] :as workspace}
                                 {:keys [name src-paths test-paths profile-src-paths profile-test-paths namespaces-test] :as environment}
                                 {:keys [env-to-bricks-to-test env-to-environments-to-test]}]
  (when (-> test-paths empty? not)
    (let [color-mode (-> workspace :settings :color-mode)
          config (->config workspace environment)
          lib-paths (resolve-deps name config color-mode)
          all-src-paths (set (concat src-paths test-paths profile-src-paths profile-test-paths))
          all-paths (concat all-src-paths lib-paths)
          bricks (concat components bases)
          bricks-to-test (env-to-bricks-to-test name)
          environments-to-test (env-to-environments-to-test name)
          run-message (run-message name components bases bricks-to-test environments-to-test color-mode)
          test-namespaces (brick-test-namespaces bricks bricks-to-test)
          env-test-namespaces (environment-test-namespaces name environments-to-test namespaces-test)
          test-statements (map ->test-statement (concat test-namespaces env-test-namespaces))
          class-loader (common/create-class-loader all-paths color-mode)]
      (if (-> test-statements empty?)
        (println (str "No tests to run for the " (color/environment name color-mode) " environment."))
        (run-test-statements name class-loader test-statements run-message color-mode)))))

(defn has-tests-to-run? [{:keys [name]} {:keys [env-to-bricks-to-test env-to-environments-to-test]}]
  (not (empty? (concat (env-to-bricks-to-test name)
                       (env-to-environments-to-test name)))))

(defn print-no-tests-to-run-if-only-dev-exists [environments]
  (when (= 1 (count environments))
   (println "  No tests to run. To run tests for 'dev', type: poly test :dev")))

(defn print-environments-to-test [environments-to-test color-mode]
  (let [environments (str/join ", " (map #(color/environment (:name %) color-mode)
                                         environments-to-test))]
    (println (str "Environments to run tests from: " environments "\n"))))

(defn run [{:keys [environments changes messages] :as workspace} color-mode]
  (if (validator/has-errors? messages)
    (validator/print-messages workspace)
    (let [start-time (time-util/current-time)
          environments-to-test (sort-by :name (filter #(has-tests-to-run? % changes) environments))]
      (if (empty? environments-to-test)
        (print-no-tests-to-run-if-only-dev-exists environments)
        (do
          (print-environments-to-test environments-to-test color-mode)
          (doseq [environment environments-to-test]
            (run-tests-for-environment workspace environment changes))))
      (time-util/print-execution-time start-time))))

(ns polylith.clj.core.test-runner.core
  (:require [clojure.string :as str]
            [clojure.tools.deps.alpha :as tools-deps]
            [polylith.clj.core.common.interface :as common]
            [polylith.clj.core.util.interface.color :as color]
            [polylith.clj.core.util.interface.str :as str-util]
            [polylith.clj.core.util.interface.time :as time-util]
            [polylith.clj.core.validator.interface :as validator])
  (:refer-clojure :exclude [test]))

(defn adjust-key [{:keys [type path version exclusions]}]
  (case type
    "maven" {:mvn/version version :exclusions (vec exclusions)}
    "local" {:local/root path}
    (throw (Exception. (str "Unknown library type: " type)))))

(defn key-as-symbol [[library version]]
  "The library names (keys) are stored as strings in the workspace
   and need to be converted back to symbols here.
   Library dependencies are stored as :type and :version and needs
   to be translated back to :mvn/version and :local/root."
  [(symbol library) (adjust-key version)])

(defn ->config [workspace {:keys [lib-deps lib-deps-test maven-repos]}]
  "Convert back to tools.deps format."
  (assoc workspace :mvn/repos maven-repos
                   :deps (into {} (map key-as-symbol (merge lib-deps lib-deps-test)))))

(defn ->test-statement [ns-name]
  (let [ns-symbol (symbol ns-name)]
    `(do (use 'clojure.test)
         (require '~ns-symbol)
         (clojure.test/run-tests '~ns-symbol))))

(defn resolve-deps [project-name {:keys [deps] :as config} color-mode]
  (try
    (into #{} (mapcat #(-> % second :paths)
                      (tools-deps/resolve-deps config {:extra-deps deps})))
    (catch Exception e
      (println (str "Couldn't resolve libraries for the " (color/project project-name color-mode) " project: " e))
      (throw e))))

(defn brick-test-namespaces [bricks test-brick-names]
  (let [brick-name->namespaces (into {} (map (juxt :name :namespaces-test) bricks))]
    (mapv :namespace (mapcat brick-name->namespaces test-brick-names))))

(defn project-test-namespaces [project-name projects-to-test namespaces-test]
  (when (contains? (set projects-to-test) project-name)
    (map :namespace namespaces-test)))

(defn run-test-statements [project-name class-loader test-statements run-message color-mode]
  (println (str run-message))
  (doseq [statement test-statements]
    (let [{:keys [error fail pass]}
          (try
            (common/eval-in class-loader statement)
            (catch Exception e
              (.printStackTrace e)
              (println (str (color/error color-mode "Couldn't run test statement") " for the " (color/project project-name color-mode) " project: " statement " " (color/error color-mode e)))))
          result-str (str "Test results: " pass " passes, " fail " failures, " error " errors.")]
      (when (or (nil? error)
                (< 0 error)
                (< 0 fail))
        (throw (Exception. (str "\n" (color/error color-mode result-str)))))
      (println (str "\n" (color/ok color-mode result-str))))))

(defn run-message [project-name components bases bricks-to-test projects-to-test color-mode]
  (let [component-names (set (map :name components))
        base-names (set (map :name bases))
        bases-to-test (filter #(contains? base-names %) bricks-to-test)
        bases-to-test-msg (when (-> bases-to-test empty? not) [(color/base (str/join ", " bases-to-test) color-mode)])
        components-to-test (filter #(contains? component-names %) bricks-to-test)
        components-to-test-msg (when (-> components-to-test empty? not) [(color/component (str/join ", " components-to-test) color-mode)])
        projects-to-test-msg (when (-> projects-to-test empty? not) [(color/project (str/join ", " projects-to-test) color-mode)])
        entities-msg (str/join ", " (concat components-to-test-msg
                                            bases-to-test-msg
                                            projects-to-test-msg))
        project-cnt (count projects-to-test)
        bricks-cnt (count bricks-to-test)
        project-msg (if (zero? project-cnt)
                      ""
                      (str " and " (str-util/count-things "project" project-cnt)))]
    (str "Running tests from the " (color/project project-name color-mode) " project, including "
         (str-util/count-things "brick" bricks-cnt) project-msg ": " entities-msg)))

(defn run-tests-for-project [{:keys [bases components] :as workspace}
                             {:keys [name src-paths test-paths namespaces-test] :as project}
                             {:keys [project-to-bricks-to-test project-to-projects-to-test]}]
  (when (-> test-paths empty? not)
    (let [color-mode (-> workspace :settings :color-mode)
          config (->config workspace project)
          lib-paths (resolve-deps name config color-mode)
          all-paths (set (concat src-paths test-paths lib-paths))
          bricks (concat components bases)
          bricks-to-test (project-to-bricks-to-test name)
          projects-to-test (project-to-projects-to-test name)
          run-message (run-message name components bases bricks-to-test projects-to-test color-mode)
          test-namespaces (brick-test-namespaces bricks bricks-to-test)
          project-test-namespaces (project-test-namespaces name projects-to-test namespaces-test)
          test-statements (map ->test-statement (concat test-namespaces project-test-namespaces))
          class-loader (common/create-class-loader all-paths color-mode)]
      (if (-> test-statements empty?)
        (println (str "No tests to run for the " (color/project name color-mode) " project."))
        (run-test-statements name class-loader test-statements run-message color-mode)))))

(defn has-tests-to-run? [{:keys [name]} {:keys [project-to-bricks-to-test project-to-projects-to-test]}]
  (not (empty? (concat (project-to-bricks-to-test name)
                       (project-to-projects-to-test name)))))

(defn print-no-tests-to-run-if-only-dev-exists [projects]
  (when (= 1 (count projects))
   (println "  No tests to run. To run tests for 'dev', type: poly test :dev")))

(defn print-projects-to-test [projects-to-test color-mode]
  (let [projects (str/join ", " (map #(color/project (:name %) color-mode)
                                     projects-to-test))]
    (println (str "Projects to run tests from: " projects "\n"))))

(defn run [{:keys [projects changes messages] :as workspace} color-mode]
  (if (validator/has-errors? messages)
    (validator/print-messages workspace)
    (let [start-time (time-util/current-time)
          projects-to-test (sort-by :name (filter #(has-tests-to-run? % changes) projects))]
      (if (empty? projects-to-test)
        (print-no-tests-to-run-if-only-dev-exists projects)
        (do
          (print-projects-to-test projects-to-test color-mode)
          (doseq [project projects-to-test]
            (run-tests-for-project workspace project changes))))
      (time-util/print-execution-time start-time))))

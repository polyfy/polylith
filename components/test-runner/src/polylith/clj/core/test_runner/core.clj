(ns polylith.clj.core.test-runner.core
  (:require [clojure.set :as set]
            [clojure.string :as str]
            [polylith.clj.core.deps.interface :as deps]
            [polylith.clj.core.common.interface :as common]
            [polylith.clj.core.test-runner-plugin.interface :as test-runner-plugin]
            [polylith.clj.core.util.interface.color :as color]
            [polylith.clj.core.util.interface.str :as str-util]
            [polylith.clj.core.util.interface.time :as time-util]
            [polylith.clj.core.validator.interface :as validator])
  (:refer-clojure :exclude [test]))

(defn ->test-statement [ns-name]
  (let [ns-symbol (symbol ns-name)]
    `(do (use 'clojure.test)
         (require '~ns-symbol)
         (clojure.test/run-tests '~ns-symbol))))

(defn resolve-deps [{:keys [name] :as project} settings is-verbose color-mode]
  (try
    (into #{} (mapcat #(-> % second :paths)
                      (deps/resolve-deps project settings is-verbose)))
    (catch Exception e
      (println (str "Couldn't resolve libraries for the " (color/project name color-mode) " project: " e))
      (throw e))))

(defn brick-test-namespaces [bricks test-brick-names]
  (let [brick-name->namespaces (into {} (map (juxt :name #(-> % :namespaces :test)) bricks))]
    (mapv :namespace (mapcat brick-name->namespaces test-brick-names))))

(defn project-test-namespaces [project-name projects-to-test namespaces]
  (when (contains? (set projects-to-test) project-name)
    (map :namespace (:test namespaces))))

(defn execute-fn [function fn-type project-name class-loader color-mode]
  (if function
    (do
      (println (str "Running test " fn-type " for the " (color/project project-name color-mode)
                    " project: " function))
      (try
        (if (= :missing-fn
               (common/eval-in class-loader
                               `(if-let [~'fun (clojure.core/requiring-resolve '~function)]
                                  (~'fun '~project-name)
                                  :missing-fn)))
          (do
            (println (color/error color-mode (str "Could not find " fn-type " function: " function)))
            false)
          (do
            (println)
            true))
        (catch Throwable t
          (let [message (str (or (some-> t .getCause) (.getMessage t)))]
            (println (color/error color-mode (str "\nTest " fn-type " failed: " message)))
            false))))
    true))

(defn run-test-statements [project-name eval-in-project test-statements run-message is-verbose color-mode]
  (println (str run-message))
  (when is-verbose (println (str "# test-statements:\n" test-statements) "\n"))

  (doseq [statement test-statements]
    (let [{:keys [error fail pass]}
          (try
            (eval-in-project statement)
            (catch Exception e
              (.printStackTrace e)
              (println (str (color/error color-mode "Couldn't run test statement") " for the " (color/project project-name color-mode) " project: " statement " " (color/error color-mode e)))))
          result-str (str "Test results: " pass " passes, " fail " failures, " error " errors.")]
      (when (or (nil? error)
                (< 0 error)
                (< 0 fail))
        (throw (Exception. (str "\n" (color/error color-mode result-str)))))
      (println (str "\n" (color/ok color-mode result-str))))))

(defn components-msg [component-names color-mode]
  (when (seq component-names)
    [(color/component (str/join ", " component-names) color-mode)]))

(defn bases-msg [base-names color-mode]
  (when (seq base-names)
    [(color/base (str/join ", " base-names) color-mode)]))

(defn run-message [project-name components bases bricks-to-test projects-to-test color-mode]
  (let [component-names (set (map :name components))
        base-names (set (map :name bases))
        bases-to-test (filter #(contains? base-names %) bricks-to-test)
        bases-to-test-msg (bases-msg bases-to-test color-mode)
        components-to-test (filter #(contains? component-names %) bricks-to-test)
        components-to-test-msg (components-msg components-to-test color-mode)
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

(defn make-default-test-runner
  [{:keys [workspace project changes _test-settings]}]
  (let [{:keys [bases components]} workspace
        {:keys [name namespaces paths]} project

        ;; TODO:
        ;; {:src [] :test [] :project-src [] :brick-src []


        {:keys [project-to-bricks-to-test project-to-projects-to-test]} changes

        ;; TODO: if the project tests aren't to be run, we might further narrow this down
        test-sources-present* (delay (-> paths :test seq))
        bricks-to-test* (delay (project-to-bricks-to-test name))
        projects-to-test* (delay (project-to-projects-to-test name))
        test-statements* (->> [(brick-test-namespaces (into components bases) @bricks-to-test*)
                               (project-test-namespaces name @projects-to-test* namespaces)]
                              (into [] (comp cat (map ->test-statement)))
                              (delay))]

    (reify test-runner-plugin/TestRunner
      (test-sources-present? [_] @test-sources-present*)

      (tests-present? [this {_eval-in-project :eval-in-project :as _opts}]
       (and (test-runner-plugin/test-sources-present? this)
            (seq @test-statements*)))

      (run-tests [this {:keys [color-mode eval-in-project is-verbose] :as opts}]
       (when (test-runner-plugin/tests-present? this opts)
         (let [run-message (run-message name components bases @bricks-to-test*
                                        @projects-to-test* color-mode)]
           (run-test-statements
            name eval-in-project @test-statements* run-message is-verbose color-mode)))))))

(defn ensure-valid-test-runner [candidate]
  (when-not (satisfies? test-runner-plugin/TestRunner candidate)
    (throw (ex-info "test runner must satisfy the TestRunner protocol" {}))))

(defn test-runner [{:keys [workspace project test-settings changes]}]
  (let [make-test-runner-sym
        (or (-> test-settings :make-test-runner)
            (-> workspace :settings :make-test-runner) ;; TODO: this doesn't work yet
            )

        ;; provide an easy way to revert to default when needed in a project
        make-test-runner-sym
        (when-not (= :default make-test-runner-sym)
          make-test-runner-sym)

        make-test-runner-with #(% {:workspace workspace
                                   :project project
                                   :changes changes
                                   :test-settings test-settings})

        test-runner
        (when make-test-runner-sym
          (-> (requiring-resolve make-test-runner-sym)
              (make-test-runner-with)
              (doto ensure-valid-test-runner)
              (try
                (catch Throwable e
                  ;; TODO: poly check could verify this config
                  ;; LET EXCEPTION THROUGH/RETHROW
                  ;; `help check` for error codes
                  (println "Warning. Unable to construct specified test runner"
                           (pr-str make-test-runner-sym) "for project" (:name project)
                           ", reverting to default test runner."
                           (ex-message e))))))]
    (or test-runner (make-test-runner-with make-default-test-runner))))

(defn run-tests-for-project [{:keys [settings] :as workspace}
                             {:keys [name paths] :as project}
                             changes
                             {:keys [setup-fn teardown-fn] :as test-settings}
                             is-verbose color-mode]
  (let [test-runner (test-runner {:workspace workspace
                                  :project project
                                  :test-settings test-settings
                                  :changes changes})]
    (when (test-runner-plugin/test-sources-present? test-runner)
      (let [lib-paths (resolve-deps project settings is-verbose color-mode)
            all-paths (into #{} cat [(:src paths) (:test paths) lib-paths])
            class-loader (common/create-class-loader all-paths color-mode)
            runner-opts {:class-loader class-loader
                         :eval-in-project #(common/eval-in class-loader %)
                         :is-verbose is-verbose
                         :color-mode color-mode}]
        (when is-verbose (println (str "# paths:\n" all-paths "\n")))
        (if-not (test-runner-plugin/tests-present? test-runner runner-opts)
          (println (str "No tests to run for the " (color/project name color-mode) " project."))
          (when (execute-fn setup-fn "setup" name class-loader color-mode)
            (try
              (test-runner-plugin/run-tests test-runner runner-opts)
              (finally
                (execute-fn teardown-fn "teardown" name class-loader color-mode)))))))))

(defn affected-by-changes? [{:keys [name]} {:keys [project-to-bricks-to-test project-to-projects-to-test]}]
  (seq (concat (project-to-bricks-to-test name)
               (project-to-projects-to-test name))))

(defn print-no-tests-to-run-if-only-dev-exists [settings projects]
  (let [git-repo? (-> settings :vcs :is-git-repo)]
    (when (= 1 (count projects))
      (if git-repo?
        (println "  No tests to run. To run tests for 'dev', type: poly test :dev")
        (println "  No tests to run. Not a git repo. Execute 'git init' + commit files and directories, to add support for testing.")))))

(defn print-projects-to-test [projects-to-test color-mode]
  (let [projects (str/join ", " (map #(color/project (:name %) color-mode)
                                     projects-to-test))]
    (println (str "Projects to run tests from: " projects))))

(defn print-execution-time [start-time]
  (println)
  (time-util/print-execution-time start-time))

(defn print-bricks-to-test [component-names base-names bricks-to-test color-mode]
  (when bricks-to-test
    (let [components-to-test (sort (set/intersection component-names (set bricks-to-test)))
          bases-to-test (sort (set/intersection base-names (set bricks-to-test)))
          components (components-msg components-to-test color-mode)
          bases (bases-msg bases-to-test color-mode)
          bricks (str/join ", " (concat components bases))]
      (println (str "Bricks to run tests for: " bricks)))))

(defn run [{:keys [components bases projects changes settings messages] :as workspace} is-verbose color-mode]
  (if (validator/has-errors? messages)
    (do (validator/print-messages workspace)
        false)
    (let [start-time (time-util/current-time)
          projects-to-test (sort-by :name (filter #(affected-by-changes? % changes) projects))
          bricks-to-test (-> workspace :user-input :selected-bricks)
          component-names (set (map :name components))
          base-names (set (map :name bases))]
      (if (empty? projects-to-test)
        (print-no-tests-to-run-if-only-dev-exists settings projects)
        (do
          (print-projects-to-test projects-to-test color-mode)
          (print-bricks-to-test component-names base-names bricks-to-test color-mode)
          (println)
          (doseq [{:keys [name] :as project} projects-to-test]
            (let [test-settings (get-in settings [:projects name :test])]
              (run-tests-for-project workspace project changes test-settings is-verbose color-mode)))))
      (print-execution-time start-time)
      true)))

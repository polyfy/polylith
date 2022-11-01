(ns polylith.clj.core.test-runner-orchestrator.core
  (:refer-clojure :exclude [test])
  (:require
   [clojure.set :as set]
   [clojure.string :as str]
   [polylith.clj.core.common.interface :as common]
   [polylith.clj.core.deps.interface :as deps]
   [polylith.clj.core.test-runner-contract.interface :as test-runner-contract]
   [polylith.clj.core.test-runner-contract.interface.initializers :as test-runner-initializers]
   [polylith.clj.core.test-runner-contract.interface.verifiers :as test-runner-verifiers]
   [polylith.clj.core.util.interface.color :as color]
   [polylith.clj.core.util.interface.time :as time-util]
   [polylith.clj.core.validator.interface :as validator]))

(defn resolve-deps [{:keys [name] :as project} settings is-verbose color-mode]
  (try
    (into #{} (mapcat #(-> % second :paths))
          (deps/resolve-deps project settings is-verbose))
    (catch Exception e
      (println (str "Couldn't resolve libraries for the " (color/project name color-mode) " project: " e))
      (throw e))))

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

(defn ->valid-test-runner-fn [{:keys [project color-mode] :as opts}]
  (fn ->valid-test-runner [ctor-sym]
    (-> ctor-sym
        (test-runner-initializers/->constructor-var)
        (#(% opts))
        (test-runner-verifiers/ensure-valid-test-runner)
        (try
          (catch Exception e
            (println (str "Could not create valid test runner for the "
                          (color/project (:name project) color-mode)
                          " project: " e))
            (throw e))))))

(defn run-tests-for-project-with-test-runner
  [{:keys [test-runner setup-delay teardown-delay color-mode runner-opts project]}]
  (let [for-project-using-runner
        (str "for the " (color/project (:name project) color-mode)
             " project using test runner: "
             (test-runner-contract/test-runner-name test-runner))]
    (if-not (test-runner-contract/tests-present? test-runner runner-opts)
      (println (str "No tests to run " for-project-using-runner "."))
      (if (deref setup-delay)
        (try
          (println (str "Running tests " for-project-using-runner "..."))
          (test-runner-contract/run-tests test-runner runner-opts)
          (catch Throwable e (deref teardown-delay) (throw e)))
        (throw (ex-info (str "Test terminated due to setup failure") {:project project}))))))

(defn ex-causes [ex]
  (str/join "; " (take-while some? (iterate ex-cause ex))))

(defn ->eval-in-project [class-loader]
  (fn [form]
    (try (common/eval-in class-loader form)
         (catch Throwable e
           (throw (ex-info
                   (str "Error while evaluating form " form
                        " in class-loader. Cause: " (ex-causes e))
                   {:form form}
                   e))))))

(defn test-opts [workspace
                 settings
                 changes
                 {:keys [name] :as project}
                 is-verbose
                 color-mode]
  {:workspace workspace
   :project project
   :changes changes
   :test-settings (get-in settings [:projects name :test])
   :is-verbose is-verbose
   :color-mode color-mode})

(defn run-tests-for-project [{:keys [workspace project test-settings is-verbose color-mode] :as opts}]
  (let [{:keys [settings]} workspace
        {:keys [name paths]} project
        {:keys [create-test-runner setup-fn teardown-fn]} test-settings
        test-runners-seeing-test-sources
        (into []
              (comp (map (->valid-test-runner-fn opts))
                    (filter test-runner-contract/test-sources-present?))
              create-test-runner)]
    (when (seq test-runners-seeing-test-sources)
      (let [lib-paths (resolve-deps project settings is-verbose color-mode)
            all-paths (into #{} cat [(:src paths) (:test paths) lib-paths])
            class-loader (common/create-class-loader all-paths color-mode)
            setup!* (delay (execute-fn setup-fn "setup" name class-loader color-mode))
            setup-failed? #(and (realized? setup!*) (not (deref setup!*)))
            setup-succeeded? #(and (realized? setup!*) (deref setup!*))
            teardown!* (delay (execute-fn teardown-fn "teardown" name class-loader color-mode))
            runner-opts (merge opts
                               {:class-loader class-loader
                                :eval-in-project (->eval-in-project class-loader)})]
        (when is-verbose (println (str "# paths:\n" all-paths "\n")))
        (doseq [current-test-runner test-runners-seeing-test-sources]
          (when-not (setup-failed?)
            (->> {:test-runner current-test-runner
                  :setup-delay setup!*
                  :teardown-delay teardown!*
                  :runner-opts runner-opts}
                 (merge opts)
                 (run-tests-for-project-with-test-runner))))
        (when (setup-succeeded?)
          (deref teardown!*))))))

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
  (let [projects (str/join ", " (mapv #(color/project (:name %) color-mode) projects-to-test))]
    (println (str "Projects to run tests from: " projects))))

(defn print-execution-time [start-time]
  (println)
  (time-util/print-execution-time start-time))

(defn components-msg [component-names color-mode]
  (when (seq component-names)
    [(color/component (str/join ", " component-names) color-mode)]))

(defn bases-msg [base-names color-mode]
  (when (seq base-names)
    [(color/base (str/join ", " base-names) color-mode)]))

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
          projects-to-test (sort-by :name (filterv #(affected-by-changes? % changes) projects))
          bricks-to-test (-> workspace :user-input :selected-bricks)
          component-names (into #{} (map :name) components)
          base-names (into #{} (map :name) bases)]
      (if (empty? projects-to-test)
        (print-no-tests-to-run-if-only-dev-exists settings projects)
        (do
          (print-projects-to-test projects-to-test color-mode)
          (print-bricks-to-test component-names base-names bricks-to-test color-mode)
          (println)
          (transduce
            (comp (map #(test-opts workspace settings changes % is-verbose color-mode))
                  (map run-tests-for-project)
                  (map #(do (System/gc) %)))
            (completing (fn [_ x] (cond-> x (not x) (reduced))))
            true
            projects-to-test)))
      (print-execution-time start-time)
      true)))

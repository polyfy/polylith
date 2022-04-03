(ns polylith.clj.core.test-runner.core
  (:refer-clojure :exclude [test])
  (:require [clojure.set :as set]
            [clojure.string :as str]
            [polylith.clj.core.common.interface :as common]
            [polylith.clj.core.deps.interface :as deps]
            [polylith.clj.core.test-runner-plugin-init.interface :as runner-init]
            [polylith.clj.core.test-runner-plugin.interface :as test-runner-plugin]
            [polylith.clj.core.test-runner.default-test-runner :as default-test-runner]
            [polylith.clj.core.test-runner.message :as msg]
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

(defn ->test-runner [{:keys [project test-settings color-mode] :as opts}]
  (-> test-settings
      (:make-test-runner)
      (runner-init/->constructor-var)
      (or default-test-runner/make)
      (#(% opts))
      (runner-init/ensure-valid-test-runner)
      (try
        (catch Exception e
          (println (str "Could not create valid test runner for the "
                        (color/project (:name project) color-mode)
                        " project: " e))))))

(defn run-tests-for-project [{:keys [workspace project test-settings is-verbose color-mode] :as opts}]
  (let [{:keys [settings]} workspace
        {:keys [name paths]} project
        {:keys [setup-fn teardown-fn]} test-settings
        test-runner (->test-runner opts)]
    (when (test-runner-plugin/test-sources-present? test-runner)
      (let [lib-paths (resolve-deps project settings is-verbose color-mode)
            all-paths (into #{} cat [(:src paths) (:test paths) lib-paths])
            class-loader (common/create-class-loader all-paths color-mode)
            runner-opts (merge opts
                               {:class-loader class-loader
                                :eval-in-project #(common/eval-in class-loader %)})]
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
  (let [projects (str/join ", " (mapv #(color/project (:name %) color-mode) projects-to-test))]
    (println (str "Projects to run tests from: " projects))))

(defn print-execution-time [start-time]
  (println)
  (time-util/print-execution-time start-time))

(defn print-bricks-to-test [component-names base-names bricks-to-test color-mode]
  (when bricks-to-test
    (let [components-to-test (sort (set/intersection component-names (set bricks-to-test)))
          bases-to-test (sort (set/intersection base-names (set bricks-to-test)))
          components (msg/components components-to-test color-mode)
          bases (msg/bases bases-to-test color-mode)
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
        (let [run-opts {:workspace workspace
                        :changes changes
                        :is-verbose is-verbose
                        :color-mode color-mode}]
          (print-projects-to-test projects-to-test color-mode)
          (print-bricks-to-test component-names base-names bricks-to-test color-mode)
          (println)
          (doseq [{:keys [name] :as project} projects-to-test]
            (->> {:project project
                  :test-settings (get-in settings [:projects name :test])}
                 (merge run-opts)
                 (run-tests-for-project)))))
      (print-execution-time start-time)
      true)))

(ns polylith.clj.core.test-runner-contract.interface)

(defprotocol TestRunner
  "Implement this protocol to supply a custom test runner.

  Runner options:
  `is-verbose`      -> A boolean indicating if we are running in verbose mode
                       or not. TestRunner can use this to print additional
                       information about the test run.
  `color-mode`      -> The color-mode that the poly tool is currently running with.
                       TestRunner is expected to respect the color mode.
  `project`         -> A map containing the project information.
  `all-paths`       -> A vector of all paths necessary to create a classpath for
                       running the tests in isolation within the context of the
                       current project.
  `setup-fn`        -> An optional setup function for tests defined in the
                       workspace config. The poly tool will run this function
                       before calling run-tests only if this is an in-process
                       TestRunner. If this is an ExternalTestRunner, the external
                       test runner should run the setup-fn.
  `teardown-fn`     -> An optional teardown function for tests defined in the
                       workspace config. The poly tool will run this function
                       after the run-tests function completes (exception or not),
                       only if this is an in-process TestRunner. If this is an
                       ExternalTestRunner, the external test runner should run
                       the teardown-fn.

  Additional options for in-process TestRunner:
  `class-loader`    -> The isolated classloader created from the `all-paths`.
                       This classloader will be used to evaluate statements within
                       the project's context. Use this if you need more granular
                       access. `eval-in-project` should be sufficient for most
                       cases.
  `eval-in-project` -> A function that takes a single form as its argument and
                       evaluates it within the project's classloader. It returns
                       the result of the evaluation. This is the primary interface
                       for running tests in the project's isolated context.

  Additional options for ExternalTestRunner:
  `process-ns`      -> The main namespace of the external test runner. This
                       namespace will be invoked as a Java subprocess.

  Usage:
  Create a constructor function that returns an instance of TestRunner or
  ExternalTestRunner:

  ```
  (defn create [{:keys [workspace project test-settings is-verbose color-mode changes]}]
    ...

    (reify
      test-runner-contract/TestRunner
      (test-runner-name [this] ...)

      (test-sources-present? [this] ...)

      (tests-present? [this runner-opts] ...)

      (run-tests [this runner-opts] ...)

      ; Optional, only if you want an external test runner
      test-runner-contract/ExternalTestRunner
      (external-process-namespace [this] ...)))
  ```

  `workspace` passed to the constructor will contain `:user-input`, which
  can be used to receive additional parameters for runtime configuration.

  Add your constructor function in the workspace.edn:

  {:test {:create-test-runner [my.namespace/create]} ; to use it globally

   :projects {; to use it only for a project
              \"project-a\" {:test {:create-test-runner [my.namespace/create]}}
              ; to reset the global setting to default
              \"project-b\" {:test {:create-test-runner [:default]}}}}"

  (test-runner-name [this]
    "Returns a printable name that the poly tool can print out for
    information purposes")

  (test-sources-present? [this]
    "The poly tool calls this first before attempting to run any tests. If
    it returns a falsy value, we short-circuit. Not even the project
    classloader will be created")

  (tests-present? [this runner-opts]
    "The poly tool calls this before calling the run-tests. If it returns a
    falsy value, run-tests won't be called. The runner-opts passed to this
    function is identical to the one passed to the run-tests. It can evaluate
    forms in the project's context.")

  (run-tests [this runner-opts]
    "It should run the tests and throw an exception if the test run is considered
     failed."))

(defprotocol ExternalTestRunner
  "Extends the `TestRunner` protocol to provide an external process namespace
  for a test runner. Polylith uses a classloader approach to run tests in
  isolation by default. `ExternalTestRunner` skips the classloaders and uses
  Java subprocesses."

  (external-process-namespace [this]
    "Returns a symbol or string identifying the main namespace of an external
    test runner. If it returns nil (default), the test runner will be an
    in-process test runner and the tests will run in an isolated classloader
    within the same process.

    When an external test runner is used, the poly tool will not create a
    classloader. The external test runner implementation should use the
    `all-paths` argument passed to the run-tests function to create a classpath
    for the Java subprocesses.

    The setup-fn and teardown-fn must be run by the external test runner
    instead of the poly tool."))

(ns polylith.clj.core.test-runner-contract.interface)

(defprotocol TestRunner
  "Implement to supply a custom test runner

  `test-sources-present?`
    - called first
    - if falsey, we short-circuit, not even the project classloader will be created

  `tests-present?`
    - if falsey, run-tests won't be called; can eval forms in the project context

  `run-tests`
    - should throw if the test run is considered failed

  Special args:

  `eval-in-project`
    - a function that takes a single form which it evaluates in the project classloader and returns its result
    - this is the primary interface for running tests in the project's context

  `class-loader`
    - the project classloader in case more granular access is needed to it


   To use a custom test runner, create a constructor that returns an instance of it:

   (defn create [{:keys [workspace project changes test-settings]}]
     ,,,
     (reify TestRunner ,,,))

   `workspace` passed to the constructor will contain `:user-input`, which
   can be used to receive additional parameters for runtime configuration.

   And in workspace.edn:

   {:test {:create-test-runner my.namespace/create} ;; to use it globally

    :projects {\"project-a\" {:test {:create-test-runner my.namespace/create}} ;; to use it only for a project
               \"project-b\" {:test {:create-test-runner :default}} ;; to reset the global setting to default
               }}"
  (test-sources-present? [this])
  (tests-present? [this {:keys [class-loader eval-in-project] :as opts}])
  (run-tests [this {:keys [class-loader color-mode eval-in-project is-verbose] :as opts}]))

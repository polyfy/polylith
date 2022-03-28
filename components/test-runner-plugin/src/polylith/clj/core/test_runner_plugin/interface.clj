(ns polylith.clj.core.test-runner-plugin.interface)

(defprotocol TestRunner
  "Implement to supply a custom test runner

  test-sources-present? - called first, if falsey, we short-circuit, not even the classloader will be created
  tests-present? - if falsey, run-tests won't be called; can eval forms in the project context
  run-tests - should throw if the test run is considered failed"
  (test-sources-present? [this])
  (tests-present? [this {:keys [eval-in-project] :as opts}])
  (run-tests [this {:keys [color-mode eval-in-project is-verbose] :as opts}]))

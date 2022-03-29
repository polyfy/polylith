(ns polylith.clj.core.test-runner-plugin.interface)


(comment

  "

  Is the interface okay?
    - throw exception when tests failed - yep, good

  How could we document the constructor function one needs to supply in code?
  - in the documentation

  How best to pass through a workspace-global setting for the runner?

  Invoke the `test` command with args that could be passed on to the runner? (verbose, focus etc)
under workspace's :user-input - link from test runner docs

  Possible improvement: own vs brick paths in project? (could come later in a separate feature)


TODO: info command x under tests integration
- if test discovery is slow we can hide it behind a flag to not only look at test sources

  "


  )

(defprotocol TestRunner
  "Implement to supply a custom test runner

  test-sources-present? - called first, if falsey, we short-circuit, not even the classloader will be created
  tests-present? - if falsey, run-tests won't be called; can eval forms in the project context
  run-tests - should throw if the test run is considered failed"
  (test-sources-present? [this])
  (tests-present? [this {:keys [eval-in-project] :as opts}])
  (run-tests [this {:keys [color-mode eval-in-project is-verbose] :as opts}]))

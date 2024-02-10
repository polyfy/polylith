(ns polylith.clj.core.api.interface.test
  "Test utility functions, primarily used by Test Runners."
  (:require [polylith.clj.core.common.interface :as common]))

(defn brick-names-to-test
  "Returns the brick names to include for a project when running the tests.
   The dependencies that are calculated per project are used to test runner
   to decide which tests to run, which means that direct and indirect dependencies
   can sometimes be disabled if :include or :exclude is set."
  [test-config all-brick-names]
  (common/brick-names-to-test test-config all-brick-names))

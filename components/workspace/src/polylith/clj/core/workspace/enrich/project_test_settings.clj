(ns ^:no-doc polylith.clj.core.workspace.enrich.project-test-settings)

(defn convert-test [project-test {:keys [test]}]
  "This function converts from the old test settings format to the new
   one for backward compatibility. It also adds the global test config as
   the project's test config if it is missing the test keyword and there
   is a global test configuration."
  (merge test (if (vector? project-test)
                {:include project-test}
                project-test)))

(def default-test-runner
  'polylith.clj.core.clojure-test-test-runner.interface/create)

(def default-test-runner-aliases
  #{nil :default})

(defn add-default-test-runner-if-missing [test]
  (cond-> test
          (empty? (-> test :create-test-runner))
          (assoc :create-test-runner [default-test-runner])))

(defn convert-create-test-runner-to-vector [test]
  (let [test-runner-configuration (-> test :create-test-runner)]
    (cond-> test
            (not (coll? test-runner-configuration))
            (update-in [:create-test-runner] vector))))

(defn alias->default-test-runner [test-runner-constructor]
  (if (contains? default-test-runner-aliases test-runner-constructor)
    default-test-runner
    test-runner-constructor))

(defn replace-default-test-runner-constructors [test-runner-constructors]
  (mapv alias->default-test-runner test-runner-constructors))

(defn update-default-test-runner-constructors [test]
  (update-in test [:create-test-runner]
             replace-default-test-runner-constructors))

(defn enrich [test settings]
  "Enrich the project's test configuration with test runners. It replaces
   default test runner aliases with the actual default test runner constructor.
   It also adds the default test runner to the projects missing a test runner."
  (-> test
      (convert-test settings)
      (convert-create-test-runner-to-vector)
      (update-default-test-runner-constructors)
      (add-default-test-runner-if-missing)))

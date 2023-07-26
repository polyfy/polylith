(ns ^:no-doc polylith.clj.core.workspace.settings.test)

(def default-test-runner
  'polylith.clj.core.clojure-test-test-runner.interface/create)

(def default-test-runner-aliases
  #{nil :default})

(defn add-default-test-runner-if-missing [project-settings]
  (cond-> project-settings

    (empty? (-> project-settings :test :create-test-runner))
    (update :test assoc :create-test-runner [default-test-runner])))

(defn convert-create-test-runner-to-vector [project-settings]
  (let [test-runner-configuration (-> project-settings :test :create-test-runner)]
    (cond-> project-settings

            (not (coll? test-runner-configuration))
            (update-in [:test :create-test-runner] vector))))

(defn alias->default-test-runner [test-runner-constructor]
  (if (contains? default-test-runner-aliases test-runner-constructor)
    default-test-runner
    test-runner-constructor))

(defn replace-default-test-runner-constructors [test-runner-constructors]
  (mapv alias->default-test-runner test-runner-constructors))

(defn update-default-test-runner-constructors [project-settings]
  (update-in project-settings [:test :create-test-runner]
             replace-default-test-runner-constructors))

(defn enrich-test-settings [acc [project-name project-settings]]
  (let [enriched-project-settings (-> project-settings
                                      (convert-create-test-runner-to-vector)
                                      (update-default-test-runner-constructors)
                                      (add-default-test-runner-if-missing))]
    (assoc acc project-name enriched-project-settings)))

(defn enrich-settings
  "Enrich the project's test configuration with test runners. It replaces
  default test runner aliases with the actual default test runner constructor.
  It also adds the default test runner to the projects missing a test runner."
  [settings]
  (let [project-settings (:projects settings)
        enriched-projects (reduce enrich-test-settings
                                  {}
                                  project-settings)]
    (assoc settings :projects enriched-projects)))

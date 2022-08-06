(ns project.setup-fails.test-setup)

(defn setup [project-name]
  (throw (Exception. (str "Setup failed for project: " project-name))))

(defn teardown [project-name]
  (println "Test teardown for project:" project-name))

(ns project.teardown-fails.test-setup)

(defn setup [project-name]
  (println "Test setup for project:" project-name))

(defn teardown [project-name]
  (throw (Exception. (str "Teardown failed for project: " project-name))))

(ns project.okay.test-setup)

(defn setup [project-name]
  (println "Test setup for project:" project-name))

(defn teardown [project-name]
  (println "Test teardown for project:" project-name))

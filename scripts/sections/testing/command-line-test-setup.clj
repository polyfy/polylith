(ns project.command-line.test-setup
  (:require [clojure.test :refer :all]))

(defn test-setup [project-name]
  (println (str "--- test setup for " project-name " ---")))

(defn test-teardown [project-name]
  (println (str "--- test teardown for " project-name " ---")))

(ns project.command-line.test-setup
  (:require [clojure.test :refer :all]))

(defn setup [project-name]
  (println (str "--- test setup for " project-name " ---")))

(defn teardown [project-name]
  (println (str "--- test teardown for " project-name " ---")))

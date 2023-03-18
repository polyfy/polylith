(ns polylith.clj.core.workspace-clj.missing-deps-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.config-reader.interface :as config-reader]
            [polylith.clj.core.command.interface :as command]
            [polylith.clj.core.user-input.interface :as user-input]
            [polylith.clj.core.workspace-clj.core :as ws-clj]))

(defn ws-from-file [filename]
  (let [input (user-input/extract-params ["ws" (str "ws-file:" filename)])]
    (command/read-workspace "." input)))

(defn workspace-from-disk [entity-type]
  (let [ws-dir "examples/illegal-configs"
        user-input (user-input/extract-params (concat ["info" (str "ws-dir:" ws-dir) ":user-home"]))]
    (:config-errors (with-redefs [config-reader/file-exists? (fn [_ type] (not= entity-type type))]
                      (ws-clj/workspace-from-disk user-input)))))

(deftest could-not-find-project-config-file
  (is (= [{:error "Could not find config file: projects/service/deps.edn"
           :name "service"
           :is-dev false
           :project-name "service"
           :project-dir "examples/illegal-configs/projects/service"
           :project-config-dir "examples/illegal-configs/projects/service"}])
      (workspace-from-disk :project)))

(deftest invalid-development-config-file
  (is (= [{:error "Validation error in ./deps.edn: {:polylith [\"missing required key\"]}"}])
      (workspace-from-disk :workspace)))

(deftest could-not-find-development-config-file
  (is (= [{:error "Could not find config file: ./deps.edn"}])
      (workspace-from-disk :development)))

(deftest could-not-find-copmponent-config-file
  (is (= [{:error "Could not find config file: components/util/deps.edn", :name "util"}])
      (workspace-from-disk :brick)))

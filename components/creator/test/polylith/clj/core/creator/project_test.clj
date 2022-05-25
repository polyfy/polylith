(ns polylith.clj.core.creator.project-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.test-helper.interface :as helper]
            [polylith.clj.core.util.interface.color :as color]))

(use-fixtures :each helper/test-setup-and-tear-down)

(deftest create-project--whith-missing-name--return-error-message
  (let [output (with-out-str
                 (helper/execute-command "" "create" "workspace" "name:ws1" "top-ns:se.example")
                 (helper/execute-command "ws1" "create" "p" "name:"))]

    (is (= (str "  A project name must be given.\n")
           (color/clean-colors output)))))

(deftest create-project--when-project-already-exists--return-error-message
  (let [output (with-out-str
                 (helper/execute-command "" "create" "workspace" "name:ws1" "top-ns:se.example")
                 (helper/execute-command "ws1" "create" "p" "name:proj1")
                 (helper/execute-command "ws1" "create" "project" "name:proj1"))]

    (is (= (str "  It's recommended to add an alias to :projects in ./workspace.edn for the proj1 project.\n"
                "  Project proj1 (or alias) already exists.\n")
           (color/clean-colors output)))))

(deftest create-project--performs-expected-actions
  (let [dir "ws1/projects/proj1"
        output (with-out-str
                 (helper/execute-command "" "create" "w" "name:ws1" "top-ns:se.example" ":create")
                 (helper/execute-command "ws1" "create" "p" "name:proj1"))]

    (is (= "  It's recommended to add an alias to :projects in ./workspace.edn for the proj1 project.\n"
           (color/clean-colors output)))

    (is (= #{".gitignore"
             ".vscode"
             ".vscode/settings.json"
             "bases"
             "bases/.keep"
             "components"
             "components/.keep"
             "deps.edn"
             "development"
             "development/src"
             "development/src/.keep"
             "logo.png"
             "projects"
             "projects/.keep"
             "projects/proj1"
             "projects/proj1/deps.edn"
             "readme.md"
             "workspace.edn"}
           (helper/paths "ws1")))

    (is (= ["{:deps {org.clojure/clojure {:mvn/version \"1.10.1\"}"
            "        org.clojure/tools.deps.alpha {:mvn/version \"0.12.985\"}}"
            ""
            " :aliases {:test {:extra-paths []"
            "                  :extra-deps  {}}}}"]
           (helper/content dir "deps.edn")))))

(ns polylith.clj.core.creator.environment-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.test-helper.interface :as helper]
            [polylith.clj.core.util.interface.color :as color]))

(use-fixtures :each helper/test-setup-and-tear-down)

(deftest create-environment--when-environment-already-exists--return-error-message
  (let [output (with-out-str
                 (helper/execute-command "" "create" "workspace" "name:ws1" "top-ns:se.example")
                 (helper/execute-command "ws1" "create" "e" "name:env1")
                 (helper/execute-command "ws1" "create" "environment" "name:env1"))]

    (is (= (str "  It's recommended to add an alias to :env->alias in ./deps.edn for the env1 environment.\n"
                "  Environment env1 (or alias) already exists.\n")
           (color/clean-colors output)))))

(deftest create-environment--performs-expected-actions
  (let [dir "ws1/environments/env1"
        output (with-out-str
                 (helper/execute-command "" "create" "w" "name:ws1" "top-ns:se.example")
                 (helper/execute-command "ws1" "create" "e" "name:env1"))]

    (is (= "  It's recommended to add an alias to :env->alias in ./deps.edn for the env1 environment.\n"
           (color/clean-colors output)))

    (is (= #{"components"
             "bases"
             "development"
             "development/src"
             "development/src/.keep"
             "environments"
             "environments/env1"
             "environments/env1/deps.edn"
             ".git"
             ".gitignore"
             "logo.png"
             "deps.edn"
             "readme.md"}
           (helper/paths "ws1")))

    (is (= [""
            "{:paths []"
            ""
            " :deps {org.clojure/clojure {:mvn/version \"1.10.1\"}"
            "        org.clojure/tools.deps.alpha {:mvn/version \"0.8.695\"}}"
            ""
            " :aliases {:test {:extra-paths []"
            "                  :extra-deps  {}}}}"]
           (helper/content dir "deps.edn")))))

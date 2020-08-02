(ns polylith.clj.core.create.environment-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.test-helper.interfc :as helper]
            [polylith.clj.core.util.interfc.color :as color]))

(use-fixtures :each helper/test-setup-and-tear-down)

(deftest create-environment--when-environment-already-exists--return-error-message
  (let [ws-name "ws1"
        output (with-out-str
                 (helper/execute-command "" "create-ws" ws-name "se.example")
                 (helper/execute-command ws-name "create-env" "env1" "env")
                 (helper/execute-command ws-name "create-env" "env1" "env"))]

    (is (= (str "It's recommended to add an alias to :env-aliases in deps.edn for the env1 environment.\n"
                "Environment env1 (or alias) already exists.\n")
           (color/clean-colors output)))))

(deftest create-environment--performs-expected-actions
  (let [ws-name "ws1"
        dir (str ws-name "/environments/env1")
        output (with-out-str
                 (helper/execute-command "" "create-ws" ws-name "se.example")
                 (helper/execute-command ws-name "create-env" "env1" "env"))]

    (is (= "It's recommended to add an alias to :env-aliases in deps.edn for the env1 environment.\n"
           (color/clean-colors output)))

    (is (= #{"components"
             "bases"
             "development"
             "development/src"
             "environments"
             "environments/env1"
             "environments/env1/deps.edn"
             ".git"
             "logo.png"
             "deps.edn"
             "readme.md"}
           (helper/paths ws-name)))

    (is (= [""
            "{:paths []"
            ""
            " :deps {org.clojure/clojure {:mvn/version \"1.10.1\"}"
            "        org.clojure/tools.deps.alpha {:mvn/version \"0.8.695\"}}"
            ""
            " :aliases {:test {:extra-paths []"
            "                  :extra-deps  {}}}}"]
           (helper/content dir "deps.edn")))))

(ns polylith.clj.core.workspace.brick-deps-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.workspace.brick-deps :as deps])
  (:refer-clojure :exclude [bases]))

(def components [{:name "change"
                  :interface {:name "change"}
                  :interface-deps ["git" "util"]}
                 {:name "command"
                  :interface {:name "command"}
                  :interface-deps ["common" "help" "test-runner" "util" "workspace"]}
                 {:name "common"
                  :interface {:name "common"}
                  :interface-deps ["util"]}
                 {:name "deps"
                  :interface {:name "deps"}
                  :interface-deps []}
                 {:name "file"
                  :interface {:name "file"}
                  :interface-deps []}
                 {:name "git"
                  :interface {:name "git"}
                  :interface-deps ["shell"]}
                 {:name "help"
                  :interface {:name "help"}
                  :interface-deps ["command" "util"]}
                 {:name "shell"
                  :interface {:name "shell"}
                  :interface-deps []}
                 {:name "test-runner"
                  :interface {:name "test-runner" :definitions []}
                  :interface-deps ["common" "file" "util"]}
                 {:name "text-table"
                  :interface {:name "text-table" :definitions []}
                  :interface-deps ["util"]}
                 {:name "util"
                  :interface {:name "util"}
                  :interface-deps []}
                 {:name "validator"
                  :interface {:name "validator"}
                  :interface-deps ["common" "deps" "util"]}
                 {:name "workspace"
                  :interface {:name "workspace"}
                  :interface-deps ["common" "deps" "file" "text-table" "util" "validator"]}
                 {:name "workspace-clj"
                  :interface {:name "workspace-clj" :definitions []}
                  :interface-deps ["common" "file" "util"]}])

(def bases [{:name "cli"
             :interface-deps ["change" "command" "file" "workspace" "workspace-clj"]}])

(def component-names ["change" "command" "common" "deps" "file" "git" "help" "shell" "test-runner" "text-table" "util" "validator" "workspace" "workspace-clj"])
(def base-names ["cli"])

(deftest deps--workspace-with-dependencies--return-dependencies-per-environment
  (is (= {"change"        {:direct     ["git"
                                        "util"]
                           :direct-ifc []
                           :indirect   ["shell"]}
          "cli"           {:direct     ["change"
                                        "command"
                                        "file"
                                        "workspace"
                                        "workspace-clj"]
                           :direct-ifc []
                           :indirect   ["common"
                                        "deps"
                                        "git"
                                        "help"
                                        "shell"
                                        "test-runner"
                                        "text-table"
                                        "util"
                                        "validator"]}
          "command"       {:circular   ["command"
                                        "help"
                                        "command"]
                           :direct     ["common"
                                        "help"
                                        "test-runner"
                                        "util"
                                        "workspace"]
                           :direct-ifc []
                           :indirect   ["command"
                                        "deps"
                                        "file"
                                        "text-table"
                                        "validator"]}
          "common"        {:direct     ["util"]
                           :direct-ifc []
                           :indirect   []}
          "deps"          {:direct     []
                           :direct-ifc []
                           :indirect   []}
          "file"          {:direct     []
                           :direct-ifc []
                           :indirect   []}
          "git"           {:direct     ["shell"]
                           :direct-ifc []
                           :indirect   []}
          "help"          {:circular   ["help"
                                        "command"
                                        "help"]
                           :direct     ["command"
                                        "util"]
                           :direct-ifc []
                           :indirect   ["common"
                                        "deps"
                                        "file"
                                        "help"
                                        "test-runner"
                                        "text-table"
                                        "validator"
                                        "workspace"]}
          "shell"         {:direct     []
                           :direct-ifc []
                           :indirect   []}
          "test-runner"   {:direct     ["common"
                                        "file"
                                        "util"]
                           :direct-ifc []
                           :indirect   []}
          "text-table"    {:direct     ["util"]
                           :direct-ifc []
                           :indirect   []}
          "util"          {:direct     []
                           :direct-ifc []
                           :indirect   []}
          "validator"     {:direct     ["common"
                                        "deps"
                                        "util"]
                           :direct-ifc []
                           :indirect   []}
          "workspace"     {:direct     ["common"
                                        "deps"
                                        "file"
                                        "text-table"
                                        "util"
                                        "validator"]
                           :direct-ifc []
                           :indirect   []}
          "workspace-clj" {:direct     ["common"
                                        "file"
                                        "util"]
                           :direct-ifc []
                           :indirect   []}}
         (deps/environment-deps component-names base-names components bases))))

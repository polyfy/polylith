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
                 {:name "validate"
                  :interface {:name "validate"}
                  :interface-deps ["common" "deps" "util"]}
                 {:name "workspace"
                  :interface {:name "workspace"}
                  :interface-deps ["common" "deps" "file" "text-table" "util" "validate"]}
                 {:name "workspace-clj"
                  :interface {:name "workspace-clj" :definitions []}
                  :interface-deps ["common" "file" "util"]}])

(def bases [{:name "cli"
             :interface-deps ["change" "command" "file" "workspace" "workspace-clj"]}
            {:name "z-jocke"
             :interface-deps ["util" "workspace" "workspace-clj"]}])

(def component-names ["change" "command" "common" "deps" "file" "git" "help" "shell" "test-runner" "text-table" "util" "validate" "workspace" "workspace-clj"])

(deftest deps--workspace-with-dependencies--return-dependencies-per-environment
  (is (= {"change"        {:direct   ["git"
                                      "util"]
                           :indirect ["shell"]}
          "cli"           {:direct   ["change"
                                      "command"
                                      "file"
                                      "workspace"
                                      "workspace-clj"]
                           :indirect ["common"
                                      "deps"
                                      "git"
                                      "help"
                                      "shell"
                                      "test-runner"
                                      "text-table"
                                      "util"
                                      "validate"]}
          "command"       {:circular ["command"
                                      "help"
                                      "command"]
                           :direct   ["common"
                                      "help"
                                      "test-runner"
                                      "util"
                                      "workspace"]
                           :indirect ["command"
                                      "deps"
                                      "file"
                                      "text-table"
                                      "validate"]}
          "common"        {:direct   ["util"]
                           :indirect []}
          "deps"          {:direct   []
                           :indirect []}
          "file"          {:direct   []
                           :indirect []}
          "git"           {:direct   ["shell"]
                           :indirect []}
          "help"          {:circular ["help"
                                      "command"
                                      "help"]
                           :direct   ["command"
                                      "util"]
                           :indirect ["common"
                                      "deps"
                                      "file"
                                      "help"
                                      "test-runner"
                                      "text-table"
                                      "validate"
                                      "workspace"]}
          "shell"         {:direct   []
                           :indirect []}
          "test-runner"   {:direct   ["common"
                                      "file"
                                      "util"]
                           :indirect []}
          "text-table"    {:direct   ["util"]
                           :indirect []}
          "util"          {:direct   []
                           :indirect []}
          "validate"      {:direct   ["common"
                                      "deps"
                                      "util"]
                           :indirect []}
          "workspace"     {:direct   ["common"
                                      "deps"
                                      "file"
                                      "text-table"
                                      "util"
                                      "validate"]
                           :indirect []}
          "workspace-clj" {:direct   ["common"
                                      "file"
                                      "util"]
                           :indirect []}
          "z-jocke"       {:direct   ["util"
                                      "workspace"
                                      "workspace-clj"]
                           :indirect ["common"
                                      "deps"
                                      "file"
                                      "text-table"
                                      "validate"]}}
         (deps/environment-deps component-names components bases))))

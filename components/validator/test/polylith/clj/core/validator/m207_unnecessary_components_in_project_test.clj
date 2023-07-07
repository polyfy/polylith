(ns polylith.clj.core.validator.m207-unnecessary-components-in-project-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.validator.m207-unnecessary-components-in-project :as m207]
            [polylith.clj.core.util.interface.color :as c]))

(def settings {:projects {"api" {}
                          "development" {:necessary []}}})

(def dev {:base-names {:src ["poly-cli"], :test ["poly-cli"]}
          :is-dev true
          :name "development"
          :type "project"
          :component-names {:src ["api"
                                  "change"
                                  "clojure-test-test-runner"
                                  "git"
                                  "path-finder"
                                  "util"]
                            :test []}
          :deps {"util" {:src {}, :test {}}
                 "clojure-test-test-runner" {:src {:direct ["util"]}
                                             :test {:direct ["util"]}}
                 "git" {:src {:direct ["util"]}, :test {:direct ["util"]}}
                 "api" {:src {:direct ["change"]
                              :indirect ["git"
                                         "path-finder"
                                         "util"]}
                        :test {}}
                 "path-finder" {:src {:direct ["util"]}, :test {:direct ["util"]}}
                 "change" {:src {:direct ["git" "path-finder" "util"], :indirect []}
                           :test {:direct ["git" "path-finder" "util"], :indirect []}}}})

(deftest dont-check-the-dev-project
  (is (= (m207/warnings "check" settings [dev] false c/none)
         [])))

(deftest check-the-dev-project
  (is (= (m207/warnings "check" settings [dev] true c/none)
         [{:code              207
           :colorized-message "Unnecessary components were found in the development project and may be removed: api, clojure-test-test-runner. To ignore this warning, execute 'poly help check' and follow the instructions for warning 207."
           :message           "Unnecessary components were found in the development project and may be removed: api, clojure-test-test-runner. To ignore this warning, execute 'poly help check' and follow the instructions for warning 207."
           :type              "warning"}])))

(deftest check-the-dev-project-exclude-necessary-components
  (let [settings (assoc-in settings [:projects "development" :necessary]
                           ["clojure-test-test-runner"])]
    (is (= (m207/warnings "check" settings [dev] true c/none)
           [{:code              207
             :colorized-message "Unnecessary components were found in the development project and may be removed: api. To ignore this warning, execute 'poly help check' and follow the instructions for warning 207."
             :message           "Unnecessary components were found in the development project and may be removed: api. To ignore this warning, execute 'poly help check' and follow the instructions for warning 207."
             :type              "warning"}]))))

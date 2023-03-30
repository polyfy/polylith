(ns polylith.clj.core.workspace.settings-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.workspace.settings :as settings]))

(def projects [{:name "car"}
               {:name "clojure"}
               {:name "backend-system"}
               {:name "banking-system"}
               {:name "helpers"}])

(deftest enrich-settings--a-set-of-projects-without-project-mapping--returns-dev-and-undefined-mappings
  (is (= (settings/enrich-settings nil projects)
         {:projects {"backend-system" {:alias "?4"
                                       :test  {:create-test-runner ['polylith.clj.core.clojure-test-test-runner.interface/create]}}
                     "banking-system" {:alias "?2"
                                       :test  {:create-test-runner ['polylith.clj.core.clojure-test-test-runner.interface/create]}}
                     "car"            {:alias "?3"
                                       :test  {:create-test-runner ['polylith.clj.core.clojure-test-test-runner.interface/create]}}
                     "clojure"        {:alias "?1"
                                       :test  {:create-test-runner ['polylith.clj.core.clojure-test-test-runner.interface/create]}}
                     "development"    {:alias "dev"
                                       :test  {:create-test-runner ['polylith.clj.core.clojure-test-test-runner.interface/create]}}
                     "helpers"        {:alias "?5"
                                       :test  {:create-test-runner ['polylith.clj.core.clojure-test-test-runner.interface/create]}}}})))

(deftest enrich-settings--a-set-of-projects-with-incomplete-project-mapping--returns-dev-and-undefined-mappings
  (is (= (settings/enrich-settings {:projects {"helpers" {:alias "h"}}} projects)
         {:projects {"backend-system" {:alias "?4"
                                       :test  {:create-test-runner ['polylith.clj.core.clojure-test-test-runner.interface/create]}}
                     "banking-system" {:alias "?2"
                                       :test  {:create-test-runner ['polylith.clj.core.clojure-test-test-runner.interface/create]}}
                     "car"            {:alias "?3"
                                       :test  {:create-test-runner ['polylith.clj.core.clojure-test-test-runner.interface/create]}}
                     "clojure"        {:alias "?1"
                                       :test  {:create-test-runner ['polylith.clj.core.clojure-test-test-runner.interface/create]}}
                     "development"    {:alias "dev"
                                       :test  {:create-test-runner ['polylith.clj.core.clojure-test-test-runner.interface/create]}}
                     "helpers"        {:alias "h"
                                       :test  {:create-test-runner ['polylith.clj.core.clojure-test-test-runner.interface/create]}}}})))

(deftest enrich-settings--a-set-of-projects-with-incomplete-project-mapping-and-with-empty-test-runner-config--returns-dev-and-undefined-mappings
  (is (= (settings/enrich-settings {:projects {"helpers" {:alias "h"
                                                          :test {:create-test-runner []}}}}
                                   projects)
         {:projects {"backend-system" {:alias "?4"
                                       :test  {:create-test-runner ['polylith.clj.core.clojure-test-test-runner.interface/create]}}
                     "banking-system" {:alias "?2"
                                       :test  {:create-test-runner ['polylith.clj.core.clojure-test-test-runner.interface/create]}}
                     "car"            {:alias "?3"
                                       :test  {:create-test-runner ['polylith.clj.core.clojure-test-test-runner.interface/create]}}
                     "clojure"        {:alias "?1"
                                       :test  {:create-test-runner ['polylith.clj.core.clojure-test-test-runner.interface/create]}}
                     "development"    {:alias "dev"
                                       :test  {:create-test-runner ['polylith.clj.core.clojure-test-test-runner.interface/create]}}
                     "helpers"        {:alias "h"
                                       :test  {:create-test-runner ['polylith.clj.core.clojure-test-test-runner.interface/create]}}}})))

(deftest enrich-settings--a-set-of-projects-with-incomplete-project-mapping-and-with-empty-test-config--returns-dev-and-undefined-mappings
  (is (= (settings/enrich-settings {:projects {"helpers" {:alias "h"
                                                          :test {}}}}
                                   projects)
         {:projects {"backend-system" {:alias "?4"
                                       :test  {:create-test-runner ['polylith.clj.core.clojure-test-test-runner.interface/create]}}
                     "banking-system" {:alias "?2"
                                       :test  {:create-test-runner ['polylith.clj.core.clojure-test-test-runner.interface/create]}}
                     "car"            {:alias "?3"
                                       :test  {:create-test-runner ['polylith.clj.core.clojure-test-test-runner.interface/create]}}
                     "clojure"        {:alias "?1"
                                       :test  {:create-test-runner ['polylith.clj.core.clojure-test-test-runner.interface/create]}}
                     "development"    {:alias "dev"
                                       :test  {:create-test-runner ['polylith.clj.core.clojure-test-test-runner.interface/create]}}
                     "helpers"        {:alias "h"
                                       :test  {:create-test-runner ['polylith.clj.core.clojure-test-test-runner.interface/create]}}}})))

(deftest enrich-settings--a-set-of-projects-with-incomplete-project-mapping-and-with-test-runner--returns-dev-and-undefined-mappings
  (is (= (settings/enrich-settings {:projects {"helpers" {:alias "h"
                                                          :test {:create-test-runner [:default]}}}}
                                   projects)
         {:projects {"backend-system" {:alias "?4"
                                       :test  {:create-test-runner ['polylith.clj.core.clojure-test-test-runner.interface/create]}}
                     "banking-system" {:alias "?2"
                                       :test  {:create-test-runner ['polylith.clj.core.clojure-test-test-runner.interface/create]}}
                     "car"            {:alias "?3"
                                       :test  {:create-test-runner ['polylith.clj.core.clojure-test-test-runner.interface/create]}}
                     "clojure"        {:alias "?1"
                                       :test  {:create-test-runner ['polylith.clj.core.clojure-test-test-runner.interface/create]}}
                     "development"    {:alias "dev"
                                       :test  {:create-test-runner ['polylith.clj.core.clojure-test-test-runner.interface/create]}}
                     "helpers"        {:alias "h"
                                       :test  {:create-test-runner ['polylith.clj.core.clojure-test-test-runner.interface/create]}}}})))

(deftest enrich-settings--a-set-of-projects-with-default-nil-and-non-vector-with-test-runner-config--returns-dev-undefined-mappings-and-correct-test-runners
  (is (= (settings/enrich-settings {:projects {"helpers" {:alias "h"
                                                          :test {:create-test-runner [:default]}}
                                               "development" {:test {:create-test-runner nil}}
                                               "clojure" {:alias "clj"
                                                          :test {:create-test-runner 'se.example/create}}
                                               "car" {:test {:create-test-runner [:default 'se.example.create nil]}}}}
                                   projects)
         {:projects {"backend-system" {:alias "?3"
                                       :test  {:create-test-runner ['polylith.clj.core.clojure-test-test-runner.interface/create]}}
                     "banking-system" {:alias "?1"
                                       :test  {:create-test-runner ['polylith.clj.core.clojure-test-test-runner.interface/create]}}
                     "car"            {:alias "?2"
                                       :test  {:create-test-runner ['polylith.clj.core.clojure-test-test-runner.interface/create
                                                                    'se.example.create
                                                                    'polylith.clj.core.clojure-test-test-runner.interface/create]}}
                     "clojure"        {:alias "clj"
                                       :test  {:create-test-runner ['se.example/create]}}
                     "development"    {:alias "dev"
                                       :test  {:create-test-runner ['polylith.clj.core.clojure-test-test-runner.interface/create]}}
                     "helpers"        {:alias "h"
                                       :test  {:create-test-runner ['polylith.clj.core.clojure-test-test-runner.interface/create]}}}})))

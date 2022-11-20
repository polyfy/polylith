(ns polylith.clj.core.workspace.settings-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.workspace.settings :as settings]))

(def projects [{:name "car"}
               {:name "clojure"}
               {:name "backend-system"}
               {:name "banking-system"}
               {:name "helpers"}])

(deftest enrich-settings--a-set-of-projects-without-project-mapping--returns-dev-and-undefined-mappings
  (is (= {:projects {"backend-system" {:alias "?4"
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
                                       :test  {:create-test-runner ['polylith.clj.core.clojure-test-test-runner.interface/create]}}}}
         (settings/enrich-settings nil projects))))

(deftest enrich-settings--a-set-of-projects-with-incomplete-project-mapping--returns-dev-and-undefined-mappings
  (is (= {:projects {"backend-system" {:alias "?4"
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
                                       :test  {:create-test-runner ['polylith.clj.core.clojure-test-test-runner.interface/create]}}}}
         (settings/enrich-settings {:projects {"helpers" {:alias "h"}}} projects))))

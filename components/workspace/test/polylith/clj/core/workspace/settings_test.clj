(ns polylith.clj.core.workspace.settings-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.workspace.settings :as settings]))

(def projects [{:name "car"}
               {:name "clojure"}
               {:name "backend-system"}
               {:name "banking-system"}
               {:name "helpers"}])

(deftest enrich-settings--a-set-of-projects-without-project-mapping--returns-dev-and-undefined-mappings
  (is (= {:projects {"backend-system" {:alias "?4"}
                     "banking-system" {:alias "?2"}
                     "car"            {:alias "?3"}
                     "clojure"        {:alias "?1"}
                     "development"    {:alias "dev"}
                     "helpers"        {:alias "?5"}}}
         (settings/enrich-settings nil projects))))

(deftest enrich-settings--a-set-of-projects-with-incomplete-project-mapping--returns-dev-and-undefined-mappings
  (is (= {:projects {"backend-system" {:alias "?4"}
                     "banking-system" {:alias "?2"}
                     "car"            {:alias "?3"}
                     "clojure"        {:alias "?1"}
                     "development"    {:alias "dev"}
                     "helpers"        {:alias "h"}}}
         (settings/enrich-settings {:projects {"helpers" {:alias "h"}}} projects))))

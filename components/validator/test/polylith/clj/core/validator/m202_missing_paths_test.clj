(ns polylith.clj.core.validator.m202-missing-paths-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.util.interface.color :as color]
            [polylith.clj.core.validator.m202-missing-paths :as m202]))

(def projects [{:name           "cli",
                :paths {:src ["wrong/path/src"]
                        :test ["illegal/path/test"]}}])

(def paths {:missing ["wrong/path/src"
                      "illegal/path/test"
                      "keep/path/test"
                      "keep/path/resources"]})

(deftest warnings--when-one-path-is-non-existing--return-a-warning
  (is (= (m202/warnings projects paths color/none)
         [{:type "warning",
           :code 202,
           :message "Missing paths was found in deps.edn for the cli project and will be ignored: \"illegal/path/test\", \"wrong/path/src\""
           :colorized-message "Missing paths was found in deps.edn for the cli project and will be ignored: \"illegal/path/test\", \"wrong/path/src\""
           :project "cli"}])))

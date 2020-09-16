(ns polylith.clj.core.validator.m202-missing-paths-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.util.interface.color :as color]
            [polylith.clj.core.validator.m202-missing-paths :as m202]))

(def environments [{:name "cli",
                    :src-paths ["wrong/path/src"]}])

(def paths {:missing ["wrong/path/src"
                      "keep/path/test"
                      "keep/path/resources"]})

(deftest warnings--when-one-path-is-non-existing--return-a-warning
  (is (= [{:type "warning",
           :code 202,
           :message "Missing paths was found in deps.edn for the cli environment and will be ignored: \"wrong/path/src\"",
           :colorized-message "Missing paths was found in deps.edn for the cli environment and will be ignored: \"wrong/path/src\"",
           :environment "cli"}]
         (m202/warnings environments paths color/none))))

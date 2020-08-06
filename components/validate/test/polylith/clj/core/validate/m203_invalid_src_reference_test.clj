(ns polylith.clj.core.validate.m203-invalid-src-reference-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.validate.m203-invalid-src-reference :as m203]
            [polylith.clj.core.util.interfc.color :as color]))

(def environments [{:name "cli",
                    :src-paths ["wrong/path/src"]}])

(deftest warnings--when-one-path-is-non-existing--return-a-warning
  (is (= [{:type "warning",
           :code 203,
           :message "Non-existing directories was found in deps.edn for the cli environment and will be ignored: \"wrong/path/src\"",
           :colorized-message "Non-existing directories was found in deps.edn for the cli environment and will be ignored: \"wrong/path/src\"",
           :environment "cli"}])
      (m203/warnings "." environments color/none)))

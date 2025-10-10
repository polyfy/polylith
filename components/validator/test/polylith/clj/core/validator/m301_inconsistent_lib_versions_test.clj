(ns polylith.clj.core.validator.m301-inconsistent-lib-versions-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.validator.m301-inconsistent-lib-versions :as sut]))

(def configs {:workspace {:validations {:inconsistent-lib-versions {:type :warning
                                                                    :exclude []}}}})

(def libraries [{:inconsistent-lib-version true
                 :name "clj-time/clj-time"
                 :version "0.15.1"}
                {:inconsistent-lib-version true
                 :name "clj-time/clj-time"
                 :version "0.15.2"}
                {:name "metosin/malli"
                 :version "0.19.2"}
                {:name "org.clojure/clojure"
                 :version "1.12.3"}])

(deftest check-inconsistent-libs
  (is (= {:type "warning"
          :code 301
          :message "Inconsistent versions detected for library 'clj-time/clj-time': 0.15.1, 0.15.2"}
         (-> (sut/messages configs libraries)
             first
             (dissoc :colorized-message)))))

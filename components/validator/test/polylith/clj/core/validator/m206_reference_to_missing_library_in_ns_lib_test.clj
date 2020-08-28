(ns polylith.clj.core.validator.m206-reference-to-missing-library-in-ns-lib-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.util.interfc.color :as color]
            [polylith.clj.core.validator.m206-reference-to-missing-library-in-ns-lib :as m206]))

(def settings {:ns->lib {"clj-time" "clj-time"
                         "honeysql" "honeysql"
                         "environ" "environ"
                         "slugger" "slugger"}})

(def environments [{:lib-deps {"environ" #:mvn{:version "1.1.0"},
                               "com.taoensso/timbre" #:mvn{:version "4.10.0"},
                               "ring/ring-json" #:mvn{:version "0.5.0-beta1"},
                               "slugger" #:mvn{:version "1.0.1"},}}
                   {:lib-deps {"clj-jwt" #:mvn{:version "0.1.1"},}}])

(deftest warnings--when-having-undefined-libraries-in-ns->lib--return-warning
  (is (= [{:type "warning"
           :code 206
           :message "Reference to missing library was found in the :ns->lib mapping: honeysql, clj-time"
           :colorized-message "Reference to missing library was found in the :ns->lib mapping: honeysql, clj-time"}]
         (m206/warnings settings environments color/none))))

(ns polylith.clj.core.validator.m205-reference-to-missing-library-in-ns-lib-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.util.interface.color :as color]
            [polylith.clj.core.validator.m205-reference-to-missing-library-in-ns-lib :as m205]))

(def settings {:ws-type :toolsdeps1
               :ns-to-lib {"clj-time" "clj-time"
                           "honeysql" "honeysql"
                           "environ" "environ"
                           "slugger" "slugger"}})

(def projects [{:lib-deps {"environ" #:mvn{:version "1.1.0"},
                           "com.taoensso/timbre" #:mvn{:version "4.10.0"},
                           "ring/ring-json" #:mvn{:version "0.5.0-beta1"},
                           "slugger" #:mvn{:version "1.0.1"},}}
               {:lib-deps {"clj-jwt" #:mvn{:version "0.1.1"},}}])

(deftest warnings--when-having-undefined-libraries-in-ns-to-lib--return-warning
  (is (= [{:type "warning"
           :code 205
           :message "Reference to missing library was found in the :ns-to-lib mapping: honeysql, clj-time"
           :colorized-message "Reference to missing library was found in the :ns-to-lib mapping: honeysql, clj-time"}]
         (m205/warnings settings projects color/none))))

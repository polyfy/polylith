(ns polylith.clj.core.validator.m206-reference-to-missing-namespaces-in-ns-lib-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.util.interfc.color :as color]
            [polylith.clj.core.validator.m206-reference-to-missing-namespace-in-ns-lib :as m206]))

(def settings {:ns->lib {"clj-time" "clj-time"
                         "compojure" "compojure/compojure"
                         "honeysql" "honeysql"
                         "environ" "environ"
                         "slugger" "slugger"}})

(def components [{:lib-dep-names ["clj-time"
                                  "metosin/spec-tools"]}
                 {:lib-dep-names ["environ"
                                  "org.clojure/clojure"]}])

(def ws-bases [{:lib-dep-names ["compojure/compojure"
                                "environ"
                                "org.clojure/clojure"]}])

(deftest warnings--when-having-undefined-libraries-in-ns->lib--return-warning
  (is (= [{:type "warning"
           :code 206
           :message "Reference to missing libraries was found in :ns->lib settings: slugger, honeysql"
           :colorized-message "Reference to missing libraries was found in :ns->lib settings: slugger, honeysql"}]
         (m206/warnings settings components ws-bases color/none))))

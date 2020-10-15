(ns polylith.clj.core.validator.m206-reference-to-missing-namespace-in-ns-lib-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.util.interface.color :as color]
            [polylith.clj.core.validator.m206-reference-to-missing-namespace-in-ns-lib :as m206]))

(def settings {:top-namespace "clojure.realworld"
               :ns-to-lib {"clj-time" "clj-time"
                           "compojure" "compojure/compojure"
                           "honeysql" "honeysql"
                           "environ" "environ"
                           "slugger" "slugger"}})

(def components [{:namespaces-src [{:imports ["clojure.set"
                                              "polylith.clj.core.change.project"]}
                                   {:imports ["clojure.java.io" "environ.core"]}
                                   {:imports ["clojure.set" "clj-time.coerce"]}
                                   {:imports ["polylith.clj.core.change.entity" "honeysql.core"]}
                                   {:imports ["polylith.clj.core.git.interface"]}
                                   {:imports ["clojure.set"]}]}
                 {:namespaces-src [{:imports ["clojure.string" "polylith.clj.core.command.message" "polylith.clj.core.creator.interface"]}
                                   {:imports ["polylith.clj.core.common.interface" "polylith.clj.core.deps.interface"]}
                                   {:imports ["polylith.clj.core.command.core"]}
                                   {:imports ["polylith.clj.core.common.interface" "polylith.clj.core.workspace.interface"]}
                                   {:imports ["clojure.pprint"]}
                                   {:imports ["polylith.clj.core.common.interface" "polylith.clj.core.test-runner.interface"]}]}])

(def ws-bases [{:lib-dep-names ["environ"
                                "slugger"]}])

(deftest warnings--when-having-undefined-names-in-ns-to-lib--return-warning
  (is (= [{:type "warning"
           :code 206
           :message "Reference to missing namespace was found in the :ns-to-lib mapping: slugger, compojure"
           :colorized-message "Reference to missing namespace was found in the :ns-to-lib mapping: slugger, compojure"}]
         (m206/warnings settings components [] color/none))))

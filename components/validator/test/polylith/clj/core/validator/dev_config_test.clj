(ns polylith.clj.core.validator.dev-config-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.validator.interface :as validator]))

(def config {:polylith {:top-namespace "polylith.clj.core"
                        :interface-ns "interface"
                        :default-profile-name "default"
                        :compact-views #{}
                        :release-tag-pattern "v[0-9]*"
                        :stable-tag-pattern "stable-*"
                        :profile-to-alias {"api" "api"
                                           "core" "core"}
                        :ns-to-lib '{me.raynes  me.raynes/fs
                                     puget      mvxcvi/puget}}
             :aliases  {:dev {:extra-paths ["development/src"
                                            "bases/cli/src"]
                              :extra-deps '{ring {:mvn/version "1.8.1"}
                                            compojure {:mvn/version "1.6.2"}}}
                        :test {:extra-paths ["bases/cli/test"
                                             "projects/command-line/test"]}
                        :+default {:extra-paths ["components/user/src"
                                                 "components/user/test"]}
                        :+remote {:extra-paths ["components/user-remote/src"
                                                "components/user-remote/test"]}
                        :poly {:main-opts ["-m" "polylith.clj.core.poly-cli.core"]
                               :extra-deps {'polyfy/polylith
                                            {:git/url   "https://github.com/polyfy/polylith.git"
                                             :sha       "78b2c77c56d1b41109d68b451069affac935200e"
                                             :deps/root "projects/poly"}}}}})

;; todo: Make sure we test the new format also
(def input-type :toolsdeps1)

(deftest valid-config--returns-nil
  (is (= nil
         (validator/validate-dev-config input-type config))))

(deftest invalid-nop-namespace--returns-error-message
  (is (= {:polylith {:top-namespace ["should be a string"]}}
         (validator/validate-dev-config input-type (assoc-in config [:polylith :top-namespace] 1)))))

(deftest invalid-compact-views--returns-error-message
  (is (= {:polylith {:compact-views ["should be a set"]}}
         (validator/validate-dev-config input-type (assoc-in config [:polylith :compact-views] 'hello)))))

(deftest ns-to-lib--return-errors-message
  (is (= {:polylith {:ns-to-lib ["invalid type"]}}
         (validator/validate-dev-config input-type (assoc-in config [:polylith :ns-to-lib] 'hello)))))

(deftest aliases-dev--return-errors-message
  (is (= {:aliases {:dev ["invalid type"]}}
         (validator/validate-dev-config input-type (assoc-in config [:aliases :dev] [1 2 3])))))

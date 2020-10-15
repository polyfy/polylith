(ns polylith.clj.core.validator.dev-config-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.validator.data :as data]))

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
                        :poly {:main-opts ["-m" "polylith.clj.core.poly_cli.poly"]
                               :extra-deps {'polyfy/polylith
                                            {:git/url   "https://github.com/polyfy/polylith.git"
                                             :sha       "78b2c77c56d1b41109d68b451069affac935200e"
                                             :deps/root "projects/poly"}}}}})

(deftest valid-config--returns-nil
  (is (= nil
         (data/validate-dev-config config))))

(deftest invalid-nop-namespace--returns-error-message
  (is (= {:polylith {:top-namespace ["should be a string"]}}
         (data/validate-dev-config (assoc-in config [:polylith :top-namespace] 1)))))

(deftest invalid-compact-views--returns-error-message
  (is (= {:polylith {:compact-views ["should be a set"]}}
         (data/validate-dev-config (assoc-in config [:polylith :compact-views] 'hello)))))

(deftest ns-to-lib--return-errors-message
  (is (= {:polylith {:ns-to-lib ["invalid type"]}}
         (data/validate-dev-config (assoc-in config [:polylith :ns-to-lib] 'hello)))))

(deftest aliases-dev--return-errors-message
  (is (= {:aliases {:dev ["invalid type"]}}
         (data/validate-dev-config (assoc-in config [:aliases :dev] [1 2 3])))))

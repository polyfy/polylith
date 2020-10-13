(ns polylith.clj.core.validator.data-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.validator.data :as data]))

(def config {:polylith {:top-namespace "polylith.clj.core"
                        :interface-ns "interface"
                        :default-profile-name "default"
                        :compact-views #{}
                        :release-tag-pattern "v[0-9]*"
                        :stable-tag-pattern "stable-*"
                        :env-to-alias {"api" "api"
                                       "core" "core"}
                        :ns-to-lib '{me.raynes  me.raynes/fs
                                     puget      mvxcvi/puget}}
             :aliases  {:dev {:extra-paths ["development/src"
                                            "bases/cli/src"]
                              :extra-deps '{ring {:mvn/version "1.8.1"}
                                            compojure {:mvn/version "1.6.2"}}}
                        :test {:extra-paths ["bases/cli/test"
                                             "environments/command-line/test"]}
                        :+default {:extra-paths ["components/user/src"
                                                 "components/user/test"]}
                        :+remote {:extra-paths ["components/user-remote/src"
                                                "components/user-remote/test"]}
                        :poly {:main-opts ["-m" "polylith.clj.core.poly_cli.poly"]
                               :extra-deps {'polyfy/polylith
                                            {:git/url   "https://github.com/polyfy/polylith.git"
                                             :sha       "78b2c77c56d1b41109d68b451069affac935200e"
                                             :deps/root "environments/poly"}}}}})

(deftest validate-deps-edn--when-passing-in-a-valid-config--return-nil
  (is (= nil
         (data/validate-deps-edn config))))

(deftest validate-deps-edn--invalid-nop-namespace--return-error-message
  (is (= {:polylith {:top-namespace ["should be a string"]}}
         (data/validate-deps-edn (assoc-in config [:polylith :top-namespace] 1)))))

(deftest validate-deps-edn--invalid-compact-views--return-error-message
  (is (= {:polylith {:compact-views ["should be a set"]}}
         (data/validate-deps-edn (assoc-in config [:polylith :compact-views] 'hello)))))

(deftest validate-deps-edn--ns-to-lib--return-error-message
  (is (= {:polylith {:ns-to-lib ["invalid type"]}}
         (data/validate-deps-edn (assoc-in config [:polylith :ns-to-lib] 'hello)))))

(deftest validate-deps-edn--aliases-dev--return-error-message
  (is (= {:aliases {:dev ["invalid type"]}}
         (data/validate-deps-edn (assoc-in config [:aliases :dev] [1 2 3])))))

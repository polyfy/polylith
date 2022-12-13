(ns polylith.clj.core.validator.datashape.toolsdeps1-project-deployable-config-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.validator.datashape.toolsdeps1 :as toolsdeps1]))

(def config {:paths   ["../../components/change/src"
                       "../../components/command/src"
                       "../../bases/poly-cli/src"]

             :deps    '{org.clojure/clojure {:mvn/version "1.10.1"}
                        org.clojure/tools.deps {:mvn/version "0.16.1264"}
                        clj-commons/fs {:mvn/version "1.6.310"}
                        mvxcvi/puget {:mvn/version "1.3.1"}}

             :aliases {:test      {:extra-paths ["../../components/change/test"
                                                 "../../components/creator/test"
                                                 "test"]
                                   :extra-deps  {}}

                       :aot       {:extra-paths ["classes"]
                                     :main-opts   ["-e" "(compile,'polylith.clj.core.poly-cli.core)"]}

                       :uberjar   {:extra-deps {'uberdeps {:mvn/version "0.1.10"}}
                                   :main-opts  ["-m" "uberdeps.uberjar"]}}})

(deftest validate-project-deployable-config--valid-config--returns-nil
  (is (= nil
         (toolsdeps1/validate-project-deployable-config config))))

(deftest validate-project-deployable-config--valid-config-withoug-deps--returns-nil
  (is (= nil
         (toolsdeps1/validate-project-deployable-config (dissoc config :deps)))))

(deftest validate-project-deployable-config--invalid-nop-namespace--returns-error-message
  (is (= {:aliases {:test ["invalid type"]}}
         (toolsdeps1/validate-project-deployable-config (assoc-in config [:aliases :test] 1)))))

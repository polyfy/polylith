(ns
  ^{:author "Mark Sto"}
  integrant.system.core-test
  (:require [clojure.string :as str]
            [clojure.test :refer :all]
            [integrant.pg-ops.interface :as pg-ops]
            [integrant.system.core :as system]
            [integrant.system.state :as state]))

(defn- get-pg-version
  []
  (pg-ops/query-version (state/get :integrant.system/data-source)))

(deftest integrant-system-lifecycle
  (comment
    "Scenario tests the production use of the Integrant system")

  (testing "System launch succeeds"
    (is (= ::system/started (system/launch!)))
    (let [system-state @state/*state]
      (is (map? system-state))
      (is (contains? system-state :system))
      (is (contains? system-state :config))))

  (testing "All system components are initialized and functional"
    (is (str/starts-with? (get-pg-version) "PostgreSQL")))

  (testing "System restart succeeds and config does not change"
    (let [old-config (get @state/*state :config)
          new-state  (state/-restart!)]
      (is (map? new-state))
      (is (contains? new-state :system))
      (is (contains? new-state :config))
      (is (= old-config (:config new-state)))))

  (testing "System shutdown succeeds"
    (is (= ::system/stopped (system/shutdown!)))
    (let [system-state @state/*state]
      (is (nil? system-state))
      (is (thrown? Exception (get-pg-version))))))

(comment
  (run-tests)
  .)

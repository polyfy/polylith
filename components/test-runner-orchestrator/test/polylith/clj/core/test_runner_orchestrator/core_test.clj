(ns polylith.clj.core.test-runner-orchestrator.core-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.test-runner-orchestrator.core :as core])
  (:import (clojure.lang ExceptionInfo)))

(defn ->mock-execute-fn [& {:keys [returns?] :or {returns? true}}]
  (let [mock-state (atom {:call-args  []
                          :call-count 0})
        mock-fn (fn [& args]
                  (swap! mock-state
                         #(-> %
                              (update :call-args conj (vec args))
                              (update :call-count inc)))
                  returns?)]
    {:mock-fn mock-fn
     :mock    mock-state}))

(deftest execute-setup-fn--external-test-runner--do-nothing-and-return-true
  (let [{:keys [mock-fn mock]} (->mock-execute-fn)
        setup-fn (fn [_project-name])]
    (with-redefs [core/execute-fn mock-fn]
      (let [result (core/execute-setup-fn {:name "test-project-name"} "light"
                                          {:setup-fn   setup-fn
                                           :process-ns 'test-process-ns})]
        (is (true? result))
        (is (zero? (:call-count @mock)))))))

(deftest execute-setup-fn--in-process-test-runner-and-no-setup-fn--do-nothing-and-return-true
  (let [{:keys [mock-fn mock]} (->mock-execute-fn)
        class-loader "test-class-loader"]
    (with-redefs [core/execute-fn mock-fn]
      (let [result (core/execute-setup-fn {:name "test-project-name"} "light"
                                          {:class-loader class-loader})]
        (is (true? result))
        (is (zero? (:call-count @mock)))))))

(deftest execute-setup-fn--in-process-test-runner-and-with-setup-fn--execute-setup-fn
  (let [{:keys [mock-fn mock]} (->mock-execute-fn)
        setup-fn (fn [_project-name])
        class-loader "test-class-loader"]
    (with-redefs [core/execute-fn mock-fn]
      (let [result (core/execute-setup-fn {:name "test-project-name"} "light"
                                          {:setup-fn     setup-fn
                                           :class-loader class-loader})]
        (is (true? result))
        (is (= (:call-count @mock) 1))
        (is (= (:call-args @mock)
               [[setup-fn "setup" "test-project-name" class-loader "light"]]))))))

(deftest execute-setup-fn--in-process-test-runner-and-with-setup-fn-returns-false--execute-setup-fn
  (let [{:keys [mock-fn mock]} (->mock-execute-fn :returns? false)
        setup-fn (fn [_project-name])
        class-loader "test-class-loader"]
    (with-redefs [core/execute-fn mock-fn]
      (let [result (core/execute-setup-fn {:name "test-project-name"} "light"
                                          {:setup-fn     setup-fn
                                           :class-loader class-loader})]
        (is (false? result))
        (is (= (:call-count @mock) 1))
        (is (= (:call-args @mock)
               [[setup-fn "setup" "test-project-name" class-loader "light"]]))))))

(deftest execute-teardown-fn--external-test-runner--do-nothing-and-return-nil
  (let [{:keys [mock-fn mock]} (->mock-execute-fn)
        teardown-fn (fn [_project-name])]
    (with-redefs [core/execute-fn mock-fn]
      (let [result (core/execute-teardown-fn {:name "test-project-name"} "light"
                                             {:teardown-fn teardown-fn
                                              :process-ns  'test-process-ns}
                                             true)]
        (is (nil? result))
        (is (zero? (:call-count @mock)))))))

(deftest execute-teardown-fn--in-process-test-runner-and-no-teardown-fn--do-nothing-and-return-nil
  (let [{:keys [mock-fn mock]} (->mock-execute-fn)
        class-loader "test-class-loader"]
    (with-redefs [core/execute-fn mock-fn]
      (let [result (core/execute-teardown-fn {:name "test-project-name"} "light"
                                             {:class-loader class-loader}
                                             false)]
        (is (nil? result))
        (is (zero? (:call-count @mock)))))))

(deftest execute-teardown-fn--in-process-test-runner-and-with-teardown-fn-and-teardown-success--execute-teardown-and-return-nil
  (let [{:keys [mock-fn mock]} (->mock-execute-fn)
        teardown-fn (fn [_project-name])
        class-loader "test-class-loader"]
    (with-redefs [core/execute-fn mock-fn]
      (let [result (core/execute-teardown-fn {:name "test-project-name"} "light"
                                             {:teardown-fn  teardown-fn
                                              :class-loader class-loader}
                                             true)]
        (is (nil? result))
        (is (= (:call-count @mock) 1))
        (is (= (:call-args @mock)
               [[teardown-fn "teardown" "test-project-name" class-loader "light"]]))))))

(deftest execute-teardown-fn--in-process-test-runner-and-with-teardown-fn-and-teardown-fail-and-throws--execute-teardown-and-throw
  (let [{:keys [mock-fn mock]} (->mock-execute-fn :returns? false)
        teardown-fn (fn [_project-name])
        class-loader "test-class-loader"]
    (with-redefs [core/execute-fn mock-fn]
      (try
        (core/execute-teardown-fn {:name "test-project-name"} "light"
                                  {:teardown-fn  teardown-fn
                                   :class-loader class-loader}
                                  true)
        (catch ExceptionInfo e
          (is (= (ex-data e)
                 {:project          {:name "test-project-name"}
                  :teardown-failed? true}))))
      (is (= (:call-count @mock) 1))
      (is (= (:call-args @mock)
             [[teardown-fn "teardown" "test-project-name" class-loader "light"]])))))

(deftest execute-teardown-fn--in-process-test-runner-and-with-teardown-fn-and-teardown-fail-and-not-throws--execute-teardown-and-return-nil
  (let [{:keys [mock-fn mock]} (->mock-execute-fn :returns? false)
        teardown-fn (fn [_project-name])
        class-loader "test-class-loader"]
    (with-redefs [core/execute-fn mock-fn]
      (let [result (core/execute-teardown-fn {:name "test-project-name"} "light"
                                             {:teardown-fn  teardown-fn
                                              :class-loader class-loader}
                                             false)]
        (is (nil? result))
        (is (= (:call-count @mock) 1))
        (is (= (:call-args @mock)
               [[teardown-fn "teardown" "test-project-name" class-loader "light"]]))))))
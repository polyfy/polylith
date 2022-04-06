(ns polylith.clj.core.validator.m109-invalid-test-runner-constructor-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.util.interface.color :as color]
            [polylith.clj.core.validator.m109-invalid-test-runner-constructor :as sut]))

(deftest no-projects-no-errors
  (is (empty? (sut/errors {} color/none))))

(defn ws [projects] {:projects projects})

(defn one-arity-ctor [_])

(deftest pristine-configuration
  (-> {"foo" {}
       "bar" {:test {}}
       "baz" {:test {:create-test-runner nil}}
       "qux" {:test {:create-test-runner :default}}
       "zol" {:test {:create-test-runner `one-arity-ctor}}}
      (ws)
      (sut/errors color/none)
      (empty?)
      (is)))

(defn good-variable-arity-ctor-1 [& _])
(defn good-variable-arity-ctor-2 [_ & _])

(defn good-multiple-arity-ctor
  ([]) ([_ _]) ([_]))

(deftest valid-ctor-arities
  (-> {"foo" {:test {:create-test-runner `one-arity-ctor}}
       "bar" {:test {:create-test-runner `good-variable-arity-ctor-1}}
       "baz" {:test {:create-test-runner `good-variable-arity-ctor-2}}
       "qux" {:test {:create-test-runner `good-multiple-arity-ctor}}}
      (ws)
      (sut/errors color/none)
      (empty?)
      (is)))

(defn error-109 [{:keys [prefix project-text colorized-project-text suffix projects ctor-spec]}]
  {:code 109
   :colorized-message (str "Invalid test runner configuration for " (or colorized-project-text project-text) ". " prefix "." suffix)
   :create-test-runner ctor-spec
   :message (str "Invalid test runner configuration for " project-text ". " prefix "." suffix)
   :projects projects
   :type "error"})

(deftest unable-to-load-ctor
  (is (= [(error-109 {:prefix "Unable to load test runner constructor unqualified-symbol"
                      :project-text "project bar"
                      :suffix " Exception: java.lang.IllegalArgumentException: Not a qualified symbol: unqualified-symbol"
                      :ctor-spec 'unqualified-symbol
                      :projects ["bar"]})
          (error-109 {:prefix "Unable to load test runner constructor non-existing.namespace/baz"
                      :project-text "project baz"
                      :suffix " Exception: java.io.FileNotFoundException: Could not locate non_existing/namespace__init.class, non_existing/namespace.clj or non_existing/namespace.cljc on classpath. Please check that namespaces with dashes use underscores in the Clojure file name."
                      :ctor-spec 'non-existing.namespace/baz
                      :projects ["baz"]})
          (error-109 {:prefix "Unable to load test runner constructor :not-a-symbol"
                      :project-text "project foo"
                      :suffix " Exception: java.lang.IllegalArgumentException: Not a qualified symbol: :not-a-symbol"
                      :ctor-spec :not-a-symbol
                      :projects ["foo"]})
          (error-109 {:prefix "Unable to load test runner constructor clojure.core/non-existing-var"
                      :project-text "project qux"
                      :suffix " Exception: clojure.lang.ExceptionInfo: Unable to resolve symbol clojure.core/non-existing-var to a var. {:symbol clojure.core/non-existing-var}"
                      :ctor-spec 'clojure.core/non-existing-var
                      :projects ["qux"]})]
         (-> {"foo" {:test {:create-test-runner :not-a-symbol}}
              "bar" {:test {:create-test-runner 'unqualified-symbol}}
              "baz" {:test {:create-test-runner 'non-existing.namespace/baz}}
              "qux" {:test {:create-test-runner 'clojure.core/non-existing-var}}}
             (ws)
             (sut/errors color/none)
             (->> (sort-by :message))))))

(defn no-suitable-arity-ctor
  ([]) ([_ _]) ([_ _ & _]))

(deftest unsuitable-ctor
  (is (= [(error-109 {:prefix "The var referred to by polylith.clj.core.validator.m109-invalid-test-runner-constructor-test/no-suitable-arity-ctor is not a valid test runner constructor"
                      :project-text "project foo"
                      :ctor-spec `no-suitable-arity-ctor
                      :projects ["foo"]})]
         (-> {"foo" {:test {:create-test-runner `no-suitable-arity-ctor}}}
             (ws)
             (sut/errors color/none)))))

(deftest project-names-grouped-and-colored
  (is (= [(error-109 {:prefix "The var referred to by polylith.clj.core.validator.m109-invalid-test-runner-constructor-test/no-suitable-arity-ctor is not a valid test runner constructor"
                      :project-text "projects bar, foo"
                      :colorized-project-text "projects \u001B[35mbar\u001B[0m, \u001B[35mfoo\u001B[0m"
                      :ctor-spec `no-suitable-arity-ctor
                      :projects ["bar" "foo"]})
          (error-109 {:prefix "Unable to load test runner constructor non-existing.namespace/baz"
                      :project-text "projects baz, qux"
                      :colorized-project-text "projects \u001B[35mbaz\u001B[0m, \u001B[35mqux\u001B[0m"
                      :ctor-spec 'non-existing.namespace/baz
                      :projects ["baz" "qux"]
                      :suffix " Exception: java.io.FileNotFoundException: Could not locate non_existing/namespace__init.class, non_existing/namespace.clj or non_existing/namespace.cljc on classpath. Please check that namespaces with dashes use underscores in the Clojure file name."})]
         (-> {"foo" {:test {:create-test-runner `no-suitable-arity-ctor}}
              "bar" {:test {:create-test-runner `no-suitable-arity-ctor}}
              "baz" {:test {:create-test-runner 'non-existing.namespace/baz}}
              "qux" {:test {:create-test-runner 'non-existing.namespace/baz}}}
             (ws)
             (sut/errors "light")
             (->> (sort-by :message))))))

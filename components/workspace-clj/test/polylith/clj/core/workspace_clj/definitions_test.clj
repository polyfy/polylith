(ns polylith.clj.core.workspace-clj.definitions-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.workspace-clj.definitions :as defs]))

(deftest filter-statements--returns-def-statements
  (let [code '((ns polylith.spec.interface
                 (:require [polylith.spec.core :as core]))
               (defn valid-config? ['config]
                 (core/valid-config? 'config)))]
    (is (= '((defn valid-config? ['config]
               (core/valid-config? 'config)))
           (defs/filter-statements code)))))

(deftest definitions--a-single-arity-defn-statement--returns-a-definition
  (is (= [{:name "ordered-map"
           :type "function"
           :parameters [{:name "&"}
                        {:name "keyvals"}]}]
         (defs/definitions "interfc"
                           '(defn ordered-map [& keyvals] (core/ordered-map keyvals))
                           "interfc"))))

(deftest definitions--a-single-arity-defn-statement-with-a-type-hint--returns-a-definition-including-type-hint
  (is (= [{:name "my-func"
           :type "function"
           :parameters [{:name "arg1"}
                        {:name "arg2", :type "^String"}]}]
         (defs/definitions "interfc"
                           '(defn my-func [arg1 ^String arg2] (core/my-func arg1 arg2))
                           "interfc"))))

(deftest definitions--a-multi-arity-defn-statement--returns-a-list-of-definitions
  (is (= [{:name "pretty-messages"
           :type "function"
           :parameters [{:name "workspace"}]}
          {:name "pretty-messages"
           :type "function"
           :parameters [{:name "messages"}
                        {:name "color-mode"}]}]

         (defs/definitions "interfc"
                           '(defn pretty-messages
                              ([workspace]
                               (msg/pretty-messages workspace))
                              ([messages color-mode]
                               (msg/pretty-messages messages color-mode)))
                           "interfc"))))

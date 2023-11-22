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
           :arglist [{:name "&"}
                     {:name "keyvals"}]}]
         (defs/definitions "interface"
                           '(defn ordered-map [& keyvals] (core/ordered-map keyvals))
                           "interface"))))

(deftest definitions--a-single-arity-defn-statement-with-a-type-hint--returns-a-definition-including-type-hint
  (is (= [{:name "my-func"
           :type "function"
           :arglist [{:name "arg1"}
                     {:name "arg2", :type "^String"}]}]
         (defs/definitions "interface"
                           '(defn my-func [arg1 ^String arg2] (core/my-func arg1 arg2))
                           "interface"))))

(deftest definitions--a-multi-arity-defn-statement--returns-a-list-of-definitions
  (is (= [{:name "pretty-messages"
           :type "function"
           :arglist [{:name "workspace"}]}
          {:name "pretty-messages"
           :type "function"
           :arglist [{:name "messages"}
                     {:name "color-mode"}]}]

         (defs/definitions "interface"
                           '(defn pretty-messages
                              ([workspace]
                               (msg/pretty-messages workspace))
                              ([messages color-mode]
                               (msg/pretty-messages messages color-mode)))
                           "interface"))))

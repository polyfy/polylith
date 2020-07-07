(ns polylith.clj.cli-tool.workspace-clj.definitions-test
  (:require [clojure.test :refer :all]
            [polylith.clj.cli-tool.workspace-clj.definitions :as defs]))

(deftest filter-statements--returns-def-statements
  (let [code '((ns polylith.spec.interface
                 (:require [polylith.spec.core :as core]))
               (defn valid-config? ['config]
                 (core/valid-config? 'config)))]
    (is (= '((defn valid-config? ['config]
               (core/valid-config? 'config)))
           (defs/filter-statements code)))))

(deftest definitions--a-single-arity-defn-statement--returns-a-definition
  (is (= [{:name "ordered-map", :type "function", :parameters ["&" "keyvals"]}]
         (defs/definitions "interfc"
                           '(defn ordered-map [& keyvals] (core/ordered-map keyvals))))))

(deftest definitions--a-multi-arity-defn-statement--returns-a-list-of-definitions
  (is (= [{:name "pretty-messages", :type "function", :parameters ["workspace"]}
          {:name "pretty-messages", :type "function", :parameters ["messages" "color-mode"]}]
         (defs/definitions "interfc"
                           '(defn pretty-messages
                              ([workspace]
                               (msg/pretty-messages workspace))
                              ([messages color-mode]
                               (msg/pretty-messages messages color-mode)))))))

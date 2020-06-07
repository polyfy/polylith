(ns polylith.workspace.validate.illegal-name-sharing-test
  (:require [clojure.test :refer :all]
            [polylith.workspace.validate.illegal-name-sharing :as illegal-name-sharing]))

(def interfaces '[{:name "mybase",
                   :declarations [{:name add-two, :type function, :signature [x]}],
                   :implemented-by ["mybase"],
                   :dependencies []}])

(def components '[{:name "mybase",
                   :type "component",
                   :imports [{:ns-path "mybase/interface.clj", :imports [mybase1.core]}
                             {:ns-path "mybase/core.clj", :imports []}],
                   :interface {:name "mybase", :declarations [{:name add-two, :type function, :signature [x]}]},
                   :dependencies []}])

(def bases [{:name "mybase", :type "base", :imports [{:ns-path "mybase/core.clj", :imports []}], :dependencies []}])

(def interface-names (map :name interfaces))

(deftest errors--when-a-base-and-a-comonent-or-interface-have-the-same-name--return-errors
  (is (= ["A Base can't have the same name as an interface or component: mybase"]
         (illegal-name-sharing/errors interface-names components bases))))

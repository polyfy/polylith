(ns polylith.clj.core.validator.m101-illegal-namespace-deps-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.util.interface.color :as color]
            [polylith.clj.core.validator.m101-illegal-namespace-deps :as m101]))

(deftest brick-errors--without-errors--returns-no-errors
  (is (= []
         (m101/brick-errors {:name "database1"
                             :type "component"}
                            "user" color/none))))

(deftest component-error--component-depends-on-a-base--returns-errors
  (is (= [{:type "error",
           :code 101,
           :message "Illegal dependency on namespace base1.core in database1.calc. Components are not allowed to depend on bases.",
           :colorized-message "Illegal dependency on namespace base1.core in database1.calc. Components are not allowed to depend on bases.",
           :bricks ["database1"]}]
         (m101/brick-errors {:name "database1"
                             :type "component"
                             :illegal-deps [{:from-ns "calc", :to-brick-id "base1", :to-type "base", :to-namespace "core"}]}
                            "ifc" color/none))))

(deftest brick-errors--with-errors--returns-errors
  (is (= [{:type "error",
           :code 101,
           :message "Illegal dependency on namespace util.core in database1.calc. Use util.ifc instead to fix the problem.",
           :colorized-message "Illegal dependency on namespace util.core in database1.calc. Use util.ifc instead to fix the problem.",
           :bricks ["database1"]}]
         (m101/brick-errors {:name "database1"
                             :type "component"
                             :illegal-deps [{:from-ns "calc", :to-brick-id "util", :to-type "component", :to-namespace "core"}]}
                            "ifc" color/none))))

(deftest brick-errors--base-depends-on-base-with-errors--returns-errors
  (is (= [{:type "error",
           :code 101,
           :message "Illegal dependency on namespace base2.core in base1.api.",
           :colorized-message "Illegal dependency on namespace base2.core in base1.api.",
           :bricks ["base1"]}]
         (m101/brick-errors {:name "base1"
                             :type "base"
                             :illegal-deps [{:from-ns "api", :to-brick-id "base2", :to-type "base", :to-namespace "core"}]}
                            "ifc" color/none))))

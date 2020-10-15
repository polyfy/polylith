(ns polylith.clj.core.change.project-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.change.project :as project]))

(def projects [{:name "cli"
                :component-names ["util" "validator"]
                :base-names ["cli"]}
               {:name "core"
                :component-names ["change" "util" "validator" "workspace"]
                :base-names []}
               {:name "dev"
                :component-names ["util" "validator" "workspace"]
                :base-names ["cli"]}])

(def changed-bricks #{"workspace" "cmd" "core"})

(deftest changed-projects--when-two-of-three-projects-contain-changed-bricks--return-the-projects-with-then-changed-bricks
  (is (= #{"core" "dev"}
         (project/indirectly-changed-project-names projects changed-bricks))))

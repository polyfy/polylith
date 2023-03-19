(ns polylith.clj.core.change.project-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.change.project :as project]))

(def projects [{:name "cli"
                :component-names {:src ["util" "validator"]}
                :base-names {:src ["cli"]}}
               {:name "core"
                :component-names {:src ["change" "util" "validator" "workspace"]}
                :base-names {}}
               {:name "dev"
                :component-names {:src ["util" "validator" "workspace"]}
                :base-names {:src ["cli"]}}])

(def changed-bricks #{"workspace" "cmd" "core"})

(deftest changed-projects--when-two-of-three-projects-contain-changed-bricks--return-the-projects-with-then-changed-bricks
  (is (= (project/indirectly-changed-project-names projects changed-bricks)
         #{"core" "dev"})))

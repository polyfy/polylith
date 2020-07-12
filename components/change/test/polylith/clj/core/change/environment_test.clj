(ns polylith.clj.core.change.environment-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.change.environment :as env]))

(def environments [{:name "cli"
                    :component-names ["util" "validate"]
                    :base-names ["cli"]}
                   {:name "core"
                    :component-names ["change" "util" "validate" "workspace"]
                    :base-names []}
                   {:name "dev"
                    :component-names ["util" "validate" "workspace"]
                    :base-names ["cli" "z-jocke"],}])

(def changed-bricks #{"workspace" "cmd" "core"})

(deftest changed-environments--when-two-of-three-environments-contain-changed-bricks--return-the-environments-with-then-changed-bricks
  (is (= #{"core" "dev"}
         (env/changed-environments environments changed-bricks))))

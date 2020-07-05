(ns polylith.clj.cli-tool.workspace-clj.import-from-disk-test
  (:require [clojure.test :refer :all]
            [polylith.clj.cli-tool.workspace-clj.namespaces-from-disk :as from-disk]))

(deftest ->namespace--given-a-namespace-path--return-brick-namespace
  (is (= "sub.my-namespace"
         (from-disk/namespace-name "workspace-root/components/user/src/"
                                   "workspace-root/components/user/src/user/sub/my_namespace.clj"))))

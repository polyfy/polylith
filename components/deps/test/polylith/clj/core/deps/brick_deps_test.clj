(ns polylith.clj.core.deps.brick-deps-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.deps.brick-deps :as brick-deps]))

(def environment {:deps {"common" {:direct ["file" "util"]
                                   :direct-ifc ["user-config"]
                                   :indirect []}
                         "file" {:direct ["util"]
                                 :direct-ifc []
                                 :indirect []}
                         "migrator" {:direct ["common" "file" "util"]
                                     :direct-ifc []
                                     :indirect []}
                         "util" {:direct []
                                 :direct-ifc []
                                 :indirect []}
                         "migrator-cli" {:direct ["file" "migrator"]
                                         :direct-ifc []
                                         :indirect ["common" "util"]}}})

(def brick->color {"util" :green
                   "migrator" :green
                   "user-config" :green
                   "file" :green})

(deftest deps
  (is (= {:dependees [["user-config" :yellow]
                      ["file" :green]
                      ["util" :green]]
          :dependers [["migrator" :green]]}
         (brick-deps/deps environment brick->color "common"))))

(ns polylith.clj.core.deps.text-table.workspace-brick-deps-table-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.deps.text-table.workspace-brick-deps-table :as ws-brick-table]))

(def workspace {:ws-dir "."
                :name "polylith"
                :settings {:color-mode "none"
                           :empty-char "·"}
                :components [{:name "common"}
                             {:name "file"}
                             {:name "lib-dep"}
                             {:name "migrator"}
                             {:name "text-table"}
                             {:name "util"}]
                :bases [{:name "migrator-cli"}
                        {:name "poly-cli"}]})

(def project {:deps {"common" {:direct ["file" "util"]
                               :direct-ifc ["user-config"]
                               :indirect []}
                     "file" {:direct ["util"]
                             :indirect []}
                     "lib-dep" {:direct ["common" "util"]
                                :indirect ["file"]}
                     "migrator" {:direct ["common" "file" "util"]
                                 :indirect []}
                     "util" {:direct []
                             :indirect []}
                     "migrator-cli" {:direct ["file" "migrator"]
                                     :indirect ["common" "util"]}}})

(deftest table--bricks-with-dependencies--returns-a-correct-matrix
  (is (= ["                u            "
          "                s            "
          "                e            "
          "                r        m   "
          "                -        i   "
          "                c  c     g   "
          "                o  o     r   "
          "                n  m  f  a  u"
          "                f  m  i  t  t"
          "                i  o  l  o  i"
          "  brick         g  n  e  r  l"
          "  ---------------------------"
          "  common        x  ·  x  ·  x"
          "  file          ·  ·  ·  ·  x"
          "  lib-dep       ·  x  +  ·  x"
          "  migrator      ·  x  x  ·  x"
          "  util          ·  ·  ·  ·  ·"
          "  migrator-cli  ·  +  x  x  +"]
         (ws-brick-table/table workspace project))))

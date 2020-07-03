(ns interface-test
  (:require [clojure.test :refer :all]
            [clojure.string :as str]
            [polylith.core.text-table.interfc :as text-table]))

(def headers ["interface" "  " "c/b" "  " "brick" "  " "loc" " " "(t)" "   " "c"])
(def alignments [:left :left :center :left :left :left :right :left :right :left :left])
(def rows [["change" "" "c" "" "change" "" "80" "" "25" "" "x"]
           ["common" "" "c" "" "common" "" "161" "" "0" "" "x"]
           ["deps" "" "c" "" "deps" "" "67" "" "135" "" "x"]
           ["file" "" "c" "" "file" "" "74" "" "0" "" "x"]
           ["git" "" "c" "" "git" "" "31" "" "17" "" "x"]
           ["shell" "" "c" "" "shell" "" "19" "" "0" "" "x"]
           ["spec" "" "c" "" "spec" "" "17" "" "0" "" "x"]
           ["test-runner" "" "c" "" "test-runner" "" "72" "" "0" "" "x"]
           ["util" "" "c" "" "util" "" "56" "" "43" "" "x"]
           ["validate" "" "c" "" "validate" "" "370" "" "693" "" "x"]
           ["workspace" "" "c" "" "workspace" "" "265" "" "77" "" "x"]
           ["workspace-clj" "" "c" "" "workspace-clj" "" "277" "" "184" "" "x"]
           ["-" "" "b" "" "cli" "" "82" "" "184" "" "x"]])

(def header-colors (repeat 11 :none))
(def row-colors (repeat 13 header-colors))

(deftest table--table-rows--should-return-a-formatted-table
  (is (= (str "interface      c/b  brick          loc (t)   c"
              "----------------------------------------------"
              "change          c   change          80  25   x"
              "common          c   common         161   0   x"
              "deps            c   deps            67 135   x"
              "file            c   file            74   0   x"
              "git             c   git             31  17   x"
              "shell           c   shell           19   0   x"
              "spec            c   spec            17   0   x"
              "test-runner     c   test-runner     72   0   x"
              "util            c   util            56  43   x"
              "validate        c   validate       370 693   x"
              "workspace       c   workspace      265  77   x"
              "workspace-clj   c   workspace-clj  277 184   x"
              "-               b   cli             82 184   x")
         (str/replace (text-table/table headers alignments rows header-colors row-colors "plain") "\n" ""))))

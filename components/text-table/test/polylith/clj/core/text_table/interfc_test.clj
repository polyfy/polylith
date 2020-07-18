(ns polylith.clj.core.text-table.interfc-test
  (:require [clojure.test :refer :all]
            [clojure.string :as str]
            [polylith.clj.core.util.interfc.color :as color]
            [polylith.clj.core.text-table.interfc :as text-table]))

(def alignments (repeat [:left :left :center :left :left :left :right :left :right :left :left]))
(def rows [["interface" "  " "c/b" "  " "brick" "  " "loc" " " "(t)" "   " "c"]
           ["-------------" "--" "---" "--" "-------------" "--" "---" "-" "---" "---" "-"]
           ["change" "" "c" "" "change" "" "80" "" "25" "" "x"]
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

(def colors (concat (repeat (repeat :none))))

(deftest table--table-rows--should-return-a-formatted-table
  (is (= ["  interface      c/b  brick          loc (t)   c"
          "  ----------------------------------------------"
          "  change          c   change          80  25   x"
          "  common          c   common         161   0   x"
          "  deps            c   deps            67 135   x"
          "  file            c   file            74   0   x"
          "  git             c   git             31  17   x"
          "  shell           c   shell           19   0   x"
          "  spec            c   spec            17   0   x"
          "  test-runner     c   test-runner     72   0   x"
          "  util            c   util            56  43   x"
          "  validate        c   validate       370 693   x"
          "  workspace       c   workspace      265  77   x"
          "  workspace-clj   c   workspace-clj  277 184   x"
          "  -               b   cli             82 184   x"]
         (str/split-lines (text-table/table "  " alignments colors rows color/none)))))

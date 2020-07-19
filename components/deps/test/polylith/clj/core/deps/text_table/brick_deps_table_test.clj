(ns polylith.clj.core.deps.text-table.brick-deps-table-test
  (:require [clojure.test :refer :all]
            [clojure.string :as str]
            [polylith.clj.core.deps.text-table.brick-deps-table :as brick-deps-table]
            [polylith.clj.core.util.interfc.color :as color]))

(def deps {:dependers [["command" :green]
                       ["cli" :blue]
                       ["z-jocke" :blue]]
           :dependees [["common" :yellow]
                       ["deps" :green]
                       ["file" :green]
                       ["text-table" :green]
                       ["user-config" :green]
                       ["util" :green]
                       ["validate" :green]]})

(def brick->color {"workspace" :green})

(deftest table--when-having-a-list-of-dependers-and-dependees--return-correct-table
  (is (= ["  used by  <  workspace  >  uses       "
          "  -------                   -----------"
          "  command                   common     "
          "  cli                       deps       "
          "  z-jocke                   file       "
          "                            text-table "
          "                            user-config"
          "                            util       "
          "                            validate   "]
         (str/split-lines
           (brick-deps-table/table deps "workspace" brick->color color/none)))))

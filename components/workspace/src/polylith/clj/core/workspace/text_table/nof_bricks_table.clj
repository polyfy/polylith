(ns polylith.clj.core.workspace.text-table.nof-bricks-table
  (:require [polylith.clj.core.text-table.interfc :as text-table]))

(def alignments [:left :right :left :left :right])
(def colors [[:yellow :none :none :purple :none]
             [:green :none :none :blue :none]])

(defn table [interfaces components bases environments color-mode]
  (let [rows [["interfaces: " (count interfaces) "   " "environments: " (count environments)]
              ["components: " (count components) "   " "bases:        " (count bases)]]]
    (text-table/table "  " alignments colors rows color-mode)))

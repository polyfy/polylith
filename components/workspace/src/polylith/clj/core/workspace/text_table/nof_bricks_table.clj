(ns polylith.clj.core.workspace.text-table.nof-bricks-table
  (:require [polylith.clj.core.text-table.interfc :as text-table]))

(def alignments [:left :right])
(def colors [[:yellow :none]
             [:green :none]
             [:blue :none]
             [:purple :none]])

(defn table [interfaces components bases environments color-mode]
  (let [rows [["interfaces:   " (count interfaces)]
              ["components:   " (count components)]
              ["bases:        " (count bases)]
              ["environments: " (count environments)]]]

    (text-table/table "  " alignments colors rows color-mode)))

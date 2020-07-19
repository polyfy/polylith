(ns polylith.clj.core.workspace.text-table.nof-bricks-table
  (:require [polylith.clj.core.text-table.interfc :as text-table]))

(def alignments [:left :right])
(def colors [[:purple :none]
             [:blue :none]
             [:green :none]
             [:yellow :none]])

(defn table [interfaces components bases environments color-mode]
  (let [rows [["environments: " (count environments)]
              ["bases:        " (count bases)]
              ["components:   " (count components)]
              ["interfaces:   " (count interfaces)]]]

    (text-table/table "  " alignments colors rows color-mode)))

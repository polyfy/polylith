(ns polylith.clj.core.workspace.text-table.count-table
  (:require [polylith.clj.core.text-table.interfc :as text-table]))

(def alignments [:left :right :left :left :right])
(def colors [[:purple :none :none :yellow :none]
             [:blue :none :none :green :none]])

(defn table [interfaces components bases environments color-mode]
  (let [rows [["environments: " (count environments) "   " "interfaces: " (count interfaces)]
              ["bases:        " (count bases) "   " "components: " (count components)]]]
    (text-table/table "  " alignments colors rows color-mode)))

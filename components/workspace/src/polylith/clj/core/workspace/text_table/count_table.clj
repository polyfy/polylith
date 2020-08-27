(ns polylith.clj.core.workspace.text-table.count-table
  (:require [polylith.clj.core.text-table.interfc :as text-table]))

(defn table [{:keys [settings environments bases components interfaces]}]
  (let [color-mode (:color-mode settings)
        cnt-env (count environments)
        cnt-base (count bases)
        cnt-comp (count components)
        cnt-ifc (count interfaces)]
    (text-table/table "  " color-mode
      (concat [(text-table/cell 1 1 "environments:" :purple)
               (text-table/cell 3 1 cnt-env)
               (text-table/cell 1 2 "bases:" :blue)
               (text-table/cell 3 2 cnt-base)
               (text-table/cell 5 1 "interfaces:" :yellow)
               (text-table/cell 7 1 cnt-ifc)
               (text-table/cell 5 2 "components:" :green)
               (text-table/cell 7 2 cnt-comp)]
              (text-table/spaces 1 [2 4 6] [" " "   " " "])))))

(defn print-table [workspace]
  (text-table/print-table (table workspace)))

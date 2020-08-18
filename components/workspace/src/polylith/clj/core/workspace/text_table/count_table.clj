(ns polylith.clj.core.workspace.text-table.count-table
  (:require [polylith.clj.core.workspace.text-table.shared :as shared]
            [polylith.clj.core.text-table.interfc :as text-table]))

(defn table [{:keys [settings environments bases components interfaces]}]
  (let [color-mode (:color-mode settings)
        cnt-env (count environments)
        cnt-base (count bases)
        cnt-comp (count components)
        cnt-ifc (count interfaces)]
    (text-table/table "  " color-mode
      (concat [(shared/standard-cell "environments:" 1 1 :purple)
               (shared/standard-cell cnt-env 3 1)
               (shared/standard-cell "bases:" 1 2 :blue)
               (shared/standard-cell cnt-base 3 2)
               (shared/standard-cell "interfaces:" 5 1 :yellow)
               (shared/standard-cell cnt-ifc 7 1)
               (shared/standard-cell "components:" 5 2 :green)
               (shared/standard-cell cnt-comp 7 2)]
              (text-table/spaces 1 [2 4 6] [" " "   " " "])))))

(defn print-table [workspace]
  (text-table/print-table (table workspace)))

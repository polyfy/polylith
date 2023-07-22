(ns polylith.clj.core.workspace.text-table.active-profiles
  (:require [clojure.string :as str]
            [polylith.clj.core.text-table.interface :as text-table]
            [polylith.clj.core.util.interface.color :as color]))

(defn table [{:keys [active-profiles color-mode]}]
  (when (seq active-profiles)
    (text-table/table "  " color-mode
                      [(text-table/empty-line 1)
                       (text-table/cell 1 2 (str "active profiles: " (color/profile (str/join ", " (sort active-profiles)) color-mode)))])))

(defn print-table [{:keys [active-profiles] :as settings}]
  (when (seq active-profiles)
    (text-table/print-table (table settings))))
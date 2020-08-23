(ns polylith.clj.core.workspace.text-table.info-tables
  (:require [clojure.string :as str]
            [polylith.clj.core.common.interfc :as common]
            [polylith.clj.core.util.interfc.color :as color]
            [polylith.clj.core.workspace.text-table.count-table :as count-table]
            [polylith.clj.core.workspace.text-table.env-table :as env-table]
            [polylith.clj.core.workspace.text-table.lib-version-table :as lib-version-table]
            [polylith.clj.core.workspace.text-table.ws-table :as ws-table]))

(defn print-active-dev-profiles [{:keys [active-dev-profiles]} {:keys [color-mode]}]
  ;; We need to filter out the empty profile that can come in as "+".
  (let [profiles (sort (filter #(not= "" %) active-dev-profiles))]
    (when (-> profiles empty? not)
      (let [s (if (= 1 (count profiles)) "" "s")]
        (println)
        (println (str "  Active dev profile" s ": " (color/profile (str/join ", " profiles) color-mode)))))))

(defn print-info [{:keys [settings messages user-input] :as workspace}]
  (let [{:keys [show-loc? show-resources?]} user-input
        color-mode (:color-mode settings)]
    (count-table/print-table workspace)
    (print-active-dev-profiles user-input settings)
    (println)
    (env-table/print-table workspace show-loc? show-resources?)
    (println)
    (ws-table/print-table workspace show-loc? show-resources?)
    (when (-> messages empty? not)
      (println)
      (println (common/pretty-messages messages color-mode)))))

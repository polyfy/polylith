(ns polylith.clj.core.workspace.text-table.info-tables
  (:require [clojure.string :as str]
            [polylith.clj.core.common.interfc :as common]
            [polylith.clj.core.util.interfc.color :as color]
            [polylith.clj.core.workspace.text-table.count-table :as count-table]
            [polylith.clj.core.workspace.text-table.env-table :as env-table]
            [polylith.clj.core.workspace.text-table.ws-table :as ws-table]))

(defn print-stable-since [{:keys [sha tag]} color-mode]
  (if tag
    (println (str "  stable since: " (color/grey color-mode (str sha " (" tag ")"))))
    (if (str/blank? sha)
      (println (str "  " (color/warning color-mode "Warning:")
                         (color/error color-mode " not a git repo!")))
      (println (str "  stable since: " (color/grey color-mode sha)))))
  (println))

(defn print-active-dev-profiles [{:keys [active-dev-profiles]} {:keys [color-mode]}]
  (when (-> active-dev-profiles empty? not)
    (let [s (if (= 1 (count active-dev-profiles)) "" "s")]
      (println)
      (println (str "  active dev profile" s ": " (color/profile (str/join ", " (sort active-dev-profiles)) color-mode))))))

(defn print-info [{:keys [settings messages user-input] :as workspace}]
  (let [{:keys [show-loc? show-resources?]} user-input
        {:keys [color-mode stable-since]} settings]
    (print-stable-since stable-since color-mode)
    (count-table/print-table workspace)
    (print-active-dev-profiles user-input settings)
    (println)
    (env-table/print-table workspace show-loc? show-resources?)
    (println)
    (ws-table/print-table workspace show-loc? show-resources?)
    (when (-> messages empty? not)
      (println)
      (println (common/pretty-messages messages color-mode)))))

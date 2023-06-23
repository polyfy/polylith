(ns polylith.clj.core.workspace.text-table.info-table
  (:require [clojure.string :as str]
            [polylith.clj.core.text-table.interface :as text-table]
            [polylith.clj.core.util.interface.color :as color]
            [polylith.clj.core.validator.interface :as validator]
            [polylith.clj.core.workspace.text-table.active-profiles :as active-profiles]
            [polylith.clj.core.workspace.text-table.number-of-entities :as number-of-entities]
            [polylith.clj.core.workspace.text-table.stable-since :as stable-since]
            [polylith.clj.core.workspace.text-table.project-table :as project-table]
            [polylith.clj.core.workspace.text-table.ws-table :as ws-table]))

(def empty-line [""])

(defn tables [{:keys [settings changes user-input] :as workspace}]
  (let [{:keys [is-show-loc is-show-resources fake-sha]} user-input
        {:keys [color-mode]} settings
        {:keys [since-sha since-tag]} changes
        since (stable-since/table (or fake-sha since-sha) since-tag color-mode)
        number-of-entities (number-of-entities/table workspace)
        profiles (active-profiles/table settings)
        project (project-table/table workspace is-show-loc is-show-resources)
        ws (ws-table/table workspace is-show-loc is-show-resources)]
    [since number-of-entities profiles empty-line project empty-line ws]))

(defn text-width [text]
  (-> text color/clean-colors count))

(defn table-width [table]
  (if (seq table)
    (apply max (map text-width table))
    0))

(defn spaces [n]
  (str/join (repeat n " ")))

(defn adjust-row-width [text max-width]
  (let [width (text-width text)]
    (if (< width max-width)
      (str (str text) (spaces (- max-width width)))
      text)))

(defn adjust-width [table max-width]
  (mapv #(adjust-row-width % max-width) table))

(defn table [workspace]
  (let [tables (tables workspace)
        max-width (apply max (map table-width tables))]
    (mapcat #(adjust-width % max-width) tables)))

(defn print-info [{:keys [messages] :as workspace}]
  (text-table/print-table (table workspace))
  (when (seq messages)
    (println)
    (validator/print-messages workspace)))

(comment
  (require '[dev.jocke :as dev])
  (def workspace dev/workspace)

  (print-info workspace)
  #__)

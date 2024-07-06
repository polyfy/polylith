(ns ^:no-doc polylith.clj.core.info.table.workspace
  (:require [clojure.string :as str]
            [polylith.clj.core.common.interface :as common]
            [polylith.clj.core.util.interface.color :as color]
            [polylith.clj.core.validator.interface :as validator]
            [polylith.clj.core.info.table.active-profiles :as active-profiles]
            [polylith.clj.core.info.table.number-of-entities :as number-of-entities]
            [polylith.clj.core.info.table.stable-since :as stable-since]
            [polylith.clj.core.info.table.project :as project-table]
            [polylith.clj.core.info.table.brick :as brick]))

(def empty-line [""])

(defn tables [{:keys [settings changes user-input] :as workspace}]
  (let [{:keys [is-show-loc is-show-resources fake-sha fake-tag]} user-input
        {:keys [color-mode]} settings
        {:keys [since-sha since-tag]} changes
        since (stable-since/table (or fake-sha since-sha) (or fake-tag since-tag) color-mode)
        number-of-entities (number-of-entities/table workspace)
        profiles (active-profiles/table settings)
        projects (project-table/table workspace is-show-loc is-show-resources)
        bricks (brick/table workspace is-show-loc is-show-resources)]
    [since number-of-entities profiles empty-line projects empty-line bricks]))

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
      (str text (spaces (- max-width width)))
      text)))

(defn adjust-width [table max-width]
  (mapv #(adjust-row-width % max-width) table))

(defn table [workspace]
  (let [tables (tables workspace)
        max-width (apply max (map table-width tables))]
    (mapcat #(adjust-width % max-width) tables)))

(defn print-messages [workspace messages]
  (when (seq messages)
    (println)
    (validator/print-messages workspace)))

(defn print-info [{:keys [messages] :as  workspace}]
  (common/print-or-save-table workspace table nil
                              (partial print-messages workspace messages)))

(comment
  (def workspace (-> dev.jocke/workspace
                     (assoc-in [:user-input :fake-sha] "aaaaa")
                     (assoc-in [:user-input :fake-tag] "")))
  (def workspace (assoc-in dev.jocke/workspace [:user-input :out] "info.png"))

  (project-table/table workspace false false)

  (print-info workspace)
  #__)


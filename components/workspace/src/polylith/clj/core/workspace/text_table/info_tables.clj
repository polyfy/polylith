(ns polylith.clj.core.workspace.text-table.info-tables
  (:require [clojure.string :as str]
            [polylith.clj.core.util.interface.color :as color]
            [polylith.clj.core.validator.interface :as validator]
            [polylith.clj.core.workspace.text-table.count-table :as count-table]
            [polylith.clj.core.workspace.text-table.project-table :as project-table]
            [polylith.clj.core.workspace.text-table.ws-table :as ws-table]))

(defn short-name [sha]
  (if (>= (count sha) 7)
    (subs sha 0 7)
    sha))

(defn print-stable-since [since-sha since-tag color-mode]
  (let [short-sha (short-name since-sha)]
    (if since-tag
      (println (str "  stable since: " (color/grey color-mode (str short-sha " | " since-tag))))
      (if (str/blank? since-sha)
        (println (str "  " (color/warning color-mode "Warning:")
                           (color/error color-mode " not a git repo!")))
        (println (str "  stable since: " (color/grey color-mode short-sha))))))
  (println))

(defn print-active-profiles [{:keys [active-profiles color-mode]}]
  (when (-> active-profiles empty? not)
    (println)
    (println (str "  active profiles: " (color/profile (str/join ", " (sort active-profiles)) color-mode)))))

(defn print-info [{:keys [settings changes messages user-input] :as workspace}]
  (let [{:keys [is-show-loc is-show-resources fake-sha]} user-input
        {:keys [color-mode]} settings
        {:keys [since-sha since-tag]} changes]
    (print-stable-since (or fake-sha since-sha) since-tag color-mode)
    (count-table/print-table workspace)
    (print-active-profiles settings)
    (println)
    (project-table/print-table workspace is-show-loc is-show-resources)
    (println)
    (ws-table/print-table workspace is-show-loc is-show-resources)
    (when (-> messages empty? not)
      (println)
      (validator/print-messages workspace))))

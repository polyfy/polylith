(ns polylith.clj.core.workspace.text-table.info-tables
  (:require [clojure.string :as str]
            [polylith.clj.core.util.interface.color :as color]
            [polylith.clj.core.validator.interface :as validator]
            [polylith.clj.core.workspace.text-table.count-table :as count-table]
            [polylith.clj.core.workspace.text-table.env-table :as env-table]
            [polylith.clj.core.workspace.text-table.ws-table :as ws-table]))

(defn short-name [sha]
  (if (>= (count sha) 7)
    (subs sha 0 7)
    sha))

(defn print-stable-since [since-sha tag color-mode]
  (let [short-sha (short-name since-sha)]
    (if tag
      (println (str "  stable since: " (color/grey color-mode (str short-sha " | " tag))))
      (if (str/blank? since-sha)
        (println (str "  " (color/warning color-mode "Warning:")
                           (color/error color-mode " not a git repo!")))
        (println (str "  stable since: " (color/grey color-mode short-sha))))))
  (println))

(defn print-active-dev-profiles [{:keys [active-dev-profiles]} {:keys [color-mode]}]
  (when (-> active-dev-profiles empty? not)
    (println)
    (println (str "  active profiles: " (color/profile (str/join ", " (sort active-dev-profiles)) color-mode)))))

(defn print-info [{:keys [settings changes messages user-input] :as workspace}]
  (let [{:keys [is-show-loc show-resources?]} user-input
        {:keys [color-mode]} settings
        {:keys [since-sha tag]} changes]
    (print-stable-since since-sha tag color-mode)
    (count-table/print-table workspace)
    (print-active-dev-profiles user-input settings)
    (println)
    (env-table/print-table workspace is-show-loc show-resources?)
    (println)
    (ws-table/print-table workspace is-show-loc show-resources?)
    (when (-> messages empty? not)
      (println)
      (validator/print-messages workspace))))

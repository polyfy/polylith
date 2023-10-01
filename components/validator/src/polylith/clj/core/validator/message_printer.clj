(ns ^:no-doc polylith.clj.core.validator.message-printer
  (:require [clojure.string :as str]
            [polylith.clj.core.util.interface.color :as color]))

(defn message [{:keys [code type colorized-message]} color-mode]
  (if (= type "error")
    (str (color/error color-mode "  Error " code ": ") colorized-message)
    (str (color/warning color-mode "  Warning " code ": ") colorized-message)))

(defn get-messages [settings messages]
   (let [color-mode (:color-mode settings color/none)]
     (str/join "\n" (map #(message % color-mode) messages))))

(defn print-messages [{:keys [settings messages]}]
  (println (get-messages settings messages)))

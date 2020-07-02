(ns polylith.cli.cmd.check
  (:require [polylith.util.interface.color :as color]))

(defn print-message [{:keys [type code colorized-message]} color-mode]
  (if (= type "error")
    (println (str (color/error color-mode "Error " code ": ") colorized-message))
    (println (str (color/warning color-mode "Warning " code ": ") colorized-message))))

(defn execute [{:keys [messages settings]}]
  (let [color-mode (:color-mode settings)]
    (if (empty? messages)
      (println (color/ok color-mode "OK"))
      (doseq [message messages]
        (print-message message color-mode)))))

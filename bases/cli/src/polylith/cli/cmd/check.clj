(ns polylith.cli.cmd.check
  (:require [polylith.common.interface.color :as color]))

(defn print-message [{:keys [type code colorized-message]}]
  (if (= type "error")
    (println (str (color/as-red "Error " code ": ") colorized-message))
    (println (str (color/as-yellow "Warning " code ": ") colorized-message))))

(defn execute [{:keys [messages]}]
  (if (empty? messages)
    (println (color/as-green "Valid!"))
    (doseq [message messages]
      (print-message message))))

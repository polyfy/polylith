(ns polylith.cli.cmd.check
  (:require [polylith.common.interface.color :as color]))

(defn print-message [{:keys [type code message]}]
  (if (= type "error")
    (println (str (color/as-red (str "Error " code ": ")) message))
    (println (str (color/as-yellow (str "Warning " code ": ")) message))))

(defn execute [{:keys [messages]}]
  (if (empty? messages)
    (println (color/as-green "Valid!"))
    (doseq [message messages]
      (print-message message))))

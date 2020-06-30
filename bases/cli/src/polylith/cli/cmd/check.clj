(ns polylith.cli.cmd.check
  (:require [polylith.common.interface.color :as color]))

(defn print-message [{:keys [type code colorized-message]}]
  (if (= type "error")
    (println (str (color/error "Error " code ": ") colorized-message))
    (println (str (color/warning "Warning " code ": ") colorized-message))))

(defn execute [{:keys [messages]}]
  (if (empty? messages)
    (println (color/ok "OK"))
    (doseq [message messages]
      (print-message message))))

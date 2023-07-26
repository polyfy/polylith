(ns ^:no-doc polylith.clj.core.util.interface.time
  (:require [polylith.clj.core.util.time :as time]))

(defn current-time []
  (time/current-time))

(defmacro tap-seconds [message expr]
  "Taps execution time in seconds + returns the evaluated expression."
  `(time/tap-seconds ~message ~expr))

(defn print-execution-time [start-time]
  (time/print-execution-time start-time))

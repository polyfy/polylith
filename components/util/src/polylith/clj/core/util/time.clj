(ns polylith.clj.core.util.time
  (:import (java.util Date)))

(defn current-time []
  (.getTime (Date.)))

(defn elapsed-time [start-time]
  (- (current-time) start-time))

(defn minutes-and-seconds [milliseconds]
  (let [sec (int (/ milliseconds 1000))
        seconds (mod sec 60)
        minutes (int (/ sec 60))]
    (if (zero? minutes)
      (format "%d seconds" seconds)
      (format "%d minutes %d seconds" minutes seconds))))

(defn print-execution-time [start-time]
  (println (str "Execution time: " (minutes-and-seconds (elapsed-time start-time)))))

(defmacro tap-seconds
  [message expr]
  `(let [start# (. System (nanoTime))
         ret# ~expr]
     (tap> (str ~message ", elapsed time: " (/ (double (- (. System (nanoTime)) start#)) 1000000000.0) " seconds"))
     ret#))

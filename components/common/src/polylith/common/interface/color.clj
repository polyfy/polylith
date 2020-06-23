(ns polylith.common.interface.color)

(def reset  "\u001B[0m")
(def black  "\u001B[30m")
(def cyan   "\u001B[36m")
(def blue   "\u001B[34m")
(def green  "\u001B[32m")
(def purple "\u001B[35m")
(def red    "\u001B[31m")
(def white  "\u001B[37m")
(def yellow "\u001B[33m")

(defn as-green [message]
  (str green message reset))

(defn as-red [message]
  (str red message reset))

(defn as-yellow [message]
  (str yellow message reset))

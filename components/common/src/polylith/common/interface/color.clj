(ns polylith.common.interface.color
  (:require [clojure.string :as str]))

(def reset  "\u001B[0m")
(def black  "\u001B[30m")
(def cyan   "\u001B[36m")
(def blue   "\u001B[34m")
(def green  "\u001B[32m")
(def grey-light "\u001B[37m")
(def grey-dark "\u001b[90m")
(def purple "\u001B[35m")
(def red    "\u001B[31m")
(def white  "\u001B[37m")
(def yellow "\u001B[33m")

(defn color [color messages]
  (str color (str/join "" messages) reset))

(defn as-blue [& messages]
  (color blue messages))

(defn as-green [& messages]
  (color green messages))

(defn as-grey-dark [& messages]
  (color grey-dark messages))

(defn as-grey-light [& messages]
  (color grey-light messages))

(defn as-purple [& messages]
  (color purple messages))

(defn as-red [& messages]
  (color red messages))

(defn as-yellow [& messages]
  (color yellow messages))


(defn brick [type brick]
  (if (= type "component")
    (as-green brick)
    (as-blue brick)))

(defn interface [ifc]
  (as-yellow ifc))

(defn component [component]
  (as-green component))

(defn base [base]
  (as-blue base))

(defn environment [env]
  (as-purple env))

(defn namespc
  ([namespace]
   (as-grey-light namespace))
  ([interface namespace]
   (as-grey-light (str interface "." namespace))))

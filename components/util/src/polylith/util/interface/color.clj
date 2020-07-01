(ns polylith.util.interface.color
  (:require [clojure.string :as str]))

(def ^:private color-reset      "\u001B[0m")
(def ^:private color-black      "\u001B[30m")
(def ^:private color-cyan       "\u001B[36m")
(def ^:private color-blue       "\u001B[34m")
(def ^:private color-green      "\u001B[32m")
(def ^:private color-grey-light "\u001B[37m")
(def ^:private color-grey-dark  "\u001b[90m")
(def ^:private color-purple     "\u001B[35m")
(def ^:private color-red        "\u001B[31m")
(def ^:private color-white      "\u001B[37m")
(def ^:private color-yellow     "\u001B[33m")

(defn- color [color messages]
  (str color (str/join "" messages) color-reset))

(defn blue [& messages]
  (color color-blue messages))

(defn green [& messages]
  (color color-green messages))

(defn grey [dark-mode? & messages]
  (if dark-mode?
    (color color-grey-light messages)
    (color color-grey-dark messages)))

(defn purple [& messages]
  (color color-purple messages))

(defn red [& messages]
  (color color-red messages))

(defn yellow [& messages]
  (color color-yellow messages))

(defn ok [& messages]
  (color color-green messages))

(defn warning [& messages]
  (color color-yellow messages))

(defn error [& messages]
  (color color-red messages))

(defn brick [type brick]
  (if (= type "component")
    (green brick)
    (blue brick)))

(defn interface [ifc]
  (yellow ifc))

(defn component [component]
  (green component))

(defn base [base]
  (blue base))

(defn environment [env]
  (purple env))

(defn namespc
  ([namespace dark-mode?]
   (grey dark-mode? namespace))
  ([interface namespace dark-mode?]
   (grey dark-mode? (str interface "." namespace))))

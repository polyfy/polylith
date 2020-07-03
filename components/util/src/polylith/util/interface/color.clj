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

(defn colored-text
  ([color color-mode messages]
   (colored-text color color color-mode messages))
  ([color-light color-dark color-mode messages]
   (condp = color-mode
     "plain" (str/join "" messages)
     "light" (color color-dark messages)
     "dark" (color color-light messages)
     (throw (Exception. (str "Invalid color mode '" color-mode "' for messages '" messages "', expected: 'plain', 'light' or 'dark'"))))))

(defn blue [color-mode & messages]
  (colored-text color-blue color-mode messages))

(defn green [color-mode & messages]
  (colored-text color-green color-mode messages))

(defn grey [color-mode & messages]
  (colored-text color-grey-light
                color-grey-dark
                color-mode messages))

(defn purple [color-mode & messages]
  (colored-text color-purple color-mode messages))

(defn red [color-mode & messages]
  (colored-text color-red color-mode messages))

(defn yellow [color-mode & messages]
  (colored-text color-yellow color-mode messages))

(defn ok [color-mode & messages]
  (colored-text color-green color-mode messages))

(defn warning [color-mode & messages]
  (colored-text color-yellow color-mode messages))

(defn error [color-mode & messages]
  (colored-text color-red color-mode messages))

(defn brick [type brick color-mode]
  (if (= type "component")
    (green color-mode brick)
    (blue color-mode brick)))

(defn interface [ifc color-mode]
  (yellow color-mode ifc))

(defn component [component color-mode]
  (green color-mode component))

(defn base [base color-mode]
  (blue color-mode base))

(defn environment [env color-mode]
  (purple color-mode env))

(defn namespc
  ([namespace color-mode]
   (grey color-mode namespace))
  ([interface namespace color-mode]
   (grey color-mode (str interface "." namespace))))

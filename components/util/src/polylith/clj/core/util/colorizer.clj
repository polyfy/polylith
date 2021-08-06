(ns polylith.clj.core.util.colorizer
  (:require [clojure.string :as str]))

(def color-reset      "\u001B[0m")
(def color-black      "\u001B[30m")
(def color-cyan       "\u001B[36m")
(def color-blue       "\u001B[34m")
(def color-green      "\u001B[32m")
(def color-grey-light "\u001B[37m")
(def color-grey-dark  "\u001b[90m")
(def color-purple     "\u001B[35m")
(def color-red        "\u001B[31m")
(def color-white      "\u001B[37m")
(def color-yellow     "\u001B[33m")

(defn- color [color messages]
  (str color (str/join "" messages) color-reset))

(defn colored-text
  ([color color-mode messages]
   (colored-text color color color-mode messages))
  ([color-light color-dark color-mode messages]
   (case color-mode
     "none" (str/join "" messages)
     "light" (color color-dark messages)
     "dark" (color color-light messages)
     (throw (Exception. (str "Invalid color mode '" color-mode "' for messages '" messages "', expected: 'none', 'light' or 'dark'"))))))

(defn- clean-color [message color]
  (str/replace message color ""))

(defn clean-colors [message]
  (when message
    (reduce clean-color message [color-reset color-black, color-cyan, color-blue
                                 color-green color-grey-light color-grey-dark
                                 color-purple color-red color-white color-yellow])))

(defn blue [color-mode messages]
  (colored-text color-blue color-mode messages))

(defn cyan [color-mode messages]
  (colored-text color-cyan color-mode messages))

(defn green [color-mode messages]
  (colored-text color-green color-mode messages))

(defn grey [color-mode messages]
  (colored-text color-grey-light
                color-grey-dark
                color-mode messages))

(defn purple [color-mode messages]
  (colored-text color-purple color-mode messages))

(defn red [color-mode messages]
  (colored-text color-red color-mode messages))

(defn yellow [color-mode messages]
  (colored-text color-yellow color-mode messages))

(defn ok [color-mode messages]
  (colored-text color-green color-mode messages))

(defn warning [color-mode messages]
  (colored-text color-yellow color-mode messages))

(defn error [color-mode messages]
  (colored-text color-red color-mode messages))

(defn interface [name color-mode]
  (yellow color-mode name))

(defn component [name color-mode]
  (green color-mode name))

(defn base [name color-mode]
  (blue color-mode name))

(defn project [name color-mode]
  (purple color-mode name))

(defn entity [type name color-mode]
  (case type
    "interface" (interface name color-mode)
    "component" (component name color-mode)
    "base" (base name color-mode)
    "project" (project name color-mode)
    name))

(defn brick [type brick color-mode]
  (entity type brick color-mode))

(defn profile [name color-mode]
  (purple color-mode name))

(defn library [name color-mode]
  (grey color-mode name))

(defn namespc
  ([name color-mode]
   (grey color-mode name))
  ([interface namespace color-mode]
   (grey color-mode (str interface "." namespace))))

(def entities->type {"projects" "project"
                     "bases" "base"
                     "components" "component"})

(defn path [path color-mode]
  (let [strings (str/split path #"/")
        [entities name] strings
        type (entities->type entities)
        src (str/join "/" (drop 2 strings))]
    (str entities "/" (entity type name color-mode) "/" src)))


(ns ^:no-doc polylith.clj.core.util.colorizer
  (:require [clojure.string :as str]
            [polylith.clj.core.util.colors :as c]))

(defn- color [color messages]
  (str color (str/join "" messages) c/color-reset))

(defn colored-text
  ([color color-mode messages]
   (colored-text color color color-mode messages))
  ([color-light color-dark color-mode messages]
   (case color-mode
     "none" (str/join "" messages)
     "light" (color color-dark messages)
     "dark" (color color-light messages)
     (throw (Exception. (str "Invalid color mode '" color-mode "' for messages '" messages "', expected: 'none', 'light' or 'dark'"))))))

(defn extract-color [message]
  (when message
    (or (ffirst (filter #(str/starts-with? message (val %))
                        c/color->code))
        :none)))

(defn- clean-color [message color]
  (str/replace message color ""))

(defn clean-colors [message]
  (when message
    (reduce clean-color message [c/color-reset c/color-black, c/color-cyan, c/color-blue
                                 c/color-green c/color-grey-light c/color-grey-dark
                                 c/color-purple c/color-red c/color-white c/color-yellow])))

(defn blue [color-mode messages]
  (colored-text c/color-blue color-mode messages))

(defn cyan [color-mode messages]
  (colored-text c/color-cyan color-mode messages))

(defn green [color-mode messages]
  (colored-text c/color-green color-mode messages))

(defn grey [color-mode messages]
  (colored-text c/color-grey-light
                c/color-grey-dark
                color-mode messages))

(defn purple [color-mode messages]
  (colored-text c/color-purple color-mode messages))

(defn red [color-mode messages]
  (colored-text c/color-red color-mode messages))

(defn yellow [color-mode messages]
  (colored-text c/color-yellow color-mode messages))

(defn ok [color-mode messages]
  (colored-text c/color-green color-mode messages))

(defn warning [color-mode messages]
  (colored-text c/color-yellow color-mode messages))

(defn error [color-mode messages]
  (colored-text c/color-red color-mode messages))

(defn interface [name color-mode]
  (yellow color-mode name))

(defn component [name color-mode]
  (green color-mode name))

(defn base [name color-mode]
  (blue color-mode name))

(defn project [name color-mode]
  (purple color-mode name))

(defn entity [type name color-mode]
  (case (keyword type)
    :interface (interface name color-mode)
    :component (component name color-mode)
    :base (base name color-mode)
    :project (project name color-mode)
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


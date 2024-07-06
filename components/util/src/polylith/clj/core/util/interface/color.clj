(ns ^:no-doc polylith.clj.core.util.interface.color
  (:require [polylith.clj.core.util.colorizer :as colorizer]
            [polylith.clj.core.util.color-splitter :as color-splitter]))

(def none "none")

(def colors {:light {:black [36 39 43]
                     :cyan [0 255 255]
                     :blue [44 104 161]
                     :green [44,157,58]
                     :grey [255 250 204]
                     :grey-light [120 120 120]
                     :grey-dark [120 120 120]
                     :purple [142 88 173]
                     :red [238 155 154]
                     :white [80 80 80]
                     :yellow [151,127,42]}
             :dark {:black [36, 39, 43]
                    :cyan [0, 255, 255]
                    :blue [119, 188, 252]
                    :green [191, 239, 197]
                    :grey [204 204 204]
                    :grey-light [204 204 204]
                    :grey-dark [204 204 204]
                    :purple [226, 174, 255]
                    :red [238, 155, 154]
                    :white [255 255 255]
                    :yellow [248, 238, 182]}})

(defn color->rgb [color-mode color]
  (let [cmode (if (= "light" color-mode) :light :dark)
        color-schema (colors cmode)]
    (color-schema color [0 255 255])))

(defn clean-colors [message]
  (colorizer/clean-colors message))

(defn extract-color [message]
  (colorizer/extract-color message))

(defn split-colors [message x font-width]
  (color-splitter/split-colors message x font-width))

(defn blue [color-mode & messages]
  (colorizer/blue color-mode messages))

(defn cyan [color-mode & messages]
  (colorizer/cyan color-mode messages))

(defn green [color-mode & messages]
  (colorizer/green color-mode messages))

(defn grey [color-mode & messages]
  (colorizer/grey color-mode messages))

(defn purple [color-mode & messages]
  (colorizer/purple color-mode messages))

(defn red [color-mode & messages]
  (colorizer/red color-mode messages))

(defn yellow [color-mode & messages]
  (colorizer/yellow color-mode messages))

(defn ok [color-mode & messages]
  (colorizer/ok color-mode messages))

(defn warning [color-mode & messages]
  (colorizer/warning color-mode messages))

(defn error [color-mode & messages]
  (colorizer/error color-mode messages))

(defn entity [type name color-mode]
  (colorizer/entity type name color-mode))

(defn brick [type name color-mode]
  (colorizer/brick type name color-mode))

(defn interface [name color-mode]
  (colorizer/interface name color-mode))

(defn component [name color-mode]
  (colorizer/component name color-mode))

(defn base [name color-mode]
  (colorizer/base name color-mode))

(defn project [name color-mode]
  (colorizer/project name color-mode))

(defn path [path color-mode]
  (colorizer/path path color-mode))

(defn profile [name color-mode]
  (colorizer/profile name color-mode))

(defn library [name color-mode]
  (colorizer/library name color-mode))

(defn namespc
  ([name color-mode]
   (colorizer/namespc name color-mode))
  ([interface namespace color-mode]
   (colorizer/namespc interface namespace color-mode))
  ([type brick namespace color-mode]
   (colorizer/namespc type brick namespace color-mode)))

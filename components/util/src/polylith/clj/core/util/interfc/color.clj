(ns polylith.clj.core.util.interfc.color
  (:require [polylith.clj.core.util.colorizer :as colorizer]))

(def none "none")

(defn clean-colors [message]
  (colorizer/clean-colors message))

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

(defn brick [type brick color-mode]
  (colorizer/brick type brick color-mode))

(defn interface [ifc color-mode]
  (colorizer/interface ifc color-mode))

(defn component [component color-mode]
  (colorizer/component component color-mode))

(defn base [base color-mode]
  (colorizer/base base color-mode))

(defn environment [env color-mode]
  (colorizer/environment env color-mode))

(defn namespc
  ([namespace color-mode]
   (colorizer/namespc namespace color-mode))
  ([interface namespace color-mode]
   (colorizer/namespc interface namespace color-mode)))

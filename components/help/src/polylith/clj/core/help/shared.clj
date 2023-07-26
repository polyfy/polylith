(ns ^:no-doc polylith.clj.core.help.shared
  (:require [polylith.clj.core.util.interface.color :as color]
            [clojure.string :as str])
  (:refer-clojure :exclude [key]))

(defn key [name color-mode]
  (color/purple color-mode name))

(defn component-ns
  ([color-mode]
   (component-ns "" color-mode))
  ([ifc-ns color-mode]
   (component-ns ifc-ns "mycomponent" color-mode))
  ([ifc-ns component-ns color-mode]
   (str (color/namespc "com.my.company" color-mode) "."
        (color/interface component-ns color-mode)
        (if (str/blank? ifc-ns)
          ""
          (str "." (color/namespc ifc-ns color-mode))))))

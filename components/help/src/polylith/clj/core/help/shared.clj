(ns polylith.clj.core.help.shared
  (:require [polylith.clj.core.util.interface.color :as color]
            [clojure.string :as str]))

(defn component-ns
  ([color-mode]
   (component-ns "" color-mode))
  ([ifc-ns color-mode]
   (component-ns ifc-ns "mycomponent" color-mode))
  ([ifc-ns component-ns color-mode]
   (str (color/namespc "com.my.company" color-mode) "."
        (color/component component-ns color-mode)
        (if (str/blank? ifc-ns)
          ""
          (str "." (color/interface ifc-ns color-mode))))))

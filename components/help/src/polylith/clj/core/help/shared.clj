(ns polylith.clj.core.help.shared
  (:require [polylith.clj.core.util.interface.color :as color]
            [clojure.string :as str]))

(defn interface-ns
  ([color-mode]
   (interface-ns "" color-mode))
  ([ifc-ns color-mode]
   (str (color/namespc "com.my.company" color-mode) "."
        (color/interface "myinterface" color-mode)
        (if (str/blank? ifc-ns)
          ""
          (str "." (color/namespc ifc-ns color-mode))))))

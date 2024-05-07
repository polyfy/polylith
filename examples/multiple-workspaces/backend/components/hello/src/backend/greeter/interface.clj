(ns backend.greeter.interface
  (:require [backend.math.interface :as math]
            [shared.util.interface :as util]))

(def pi math/pi)

(defn greeting [name]
  (println (util/with-question-and-exclamation-mark (str "Hello " name))))

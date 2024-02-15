(ns backend.greeter.interface
  (:require [shared.util.interface :as util]))

(defn greeting [name]
  (println (util/with-exclamation-mark (str "Hello " name))))

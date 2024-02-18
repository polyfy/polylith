(ns backend.cli.core
  (:require [backend.greeter.interface :as greeter])
  (:gen-class))

(defn -main [& args]
  (greeter/greeting (first args)))

(comment
  (-main "James")
  #__)

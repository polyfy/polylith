(ns backend.cli.core
  (:require [backend.greeter.interface :as greeter])
  (:gen-class))

(defn -main [& args]
  (greeter/hello (first args)))

(comment
  (-main "James")
  #__)

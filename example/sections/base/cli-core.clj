(ns se.example.cli.core
  (:require [se.example.user.interface :as user])
  (:gen-class))

(defn -main [& args]
  (println (user-core/hello (first args)))
  (System/exit 0))

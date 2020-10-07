(ns se.example.user-api.api
  (:require [se.example.user.interface :as user]))

(defn hello-remote [name]
      (user-core/hello (str name " - from the server")))

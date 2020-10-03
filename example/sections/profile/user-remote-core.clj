(ns se.example.user.core
  (:require [slacker.client :as client]))

(declare hello-remote)

(defn hello [name]
      (let [connection (client/slackerc "localhost:2104")
            _ (client/defn-remote connection se.example.user-api.api/hello-remote)]
               (hello-remote name)))

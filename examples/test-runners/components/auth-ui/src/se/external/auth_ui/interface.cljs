(ns se.external.auth-ui.interface
  (:require [se.external.auth-ui.views :as views]))

(defn login
  []
  [views/login])

(ns com.localdep.invoicer.interface
  (:require [com.localdep.invoicer.core :as core]))

(defn do-invoicing []
  (core/do-invoicing))

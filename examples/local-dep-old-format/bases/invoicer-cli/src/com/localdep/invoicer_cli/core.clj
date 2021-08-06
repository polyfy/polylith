(ns com.localdep.invoicer-cli.core
  (:require [com.localdep.invoicer.interface :as invoicer]))

(defn -main [& args]
  (invoicer/do-invoicing))

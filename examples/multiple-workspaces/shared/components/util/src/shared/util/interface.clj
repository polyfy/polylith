(ns shared.util.interface
  (:require [shared2.util.interface.sub :as sub-util]))

(defn with-question-and-exclamation-mark [string]
  (str (sub-util/with-question-mark string) "!"))

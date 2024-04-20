(ns shared.util.interface
  (:require [shared2.util.interface :as util]))

(defn with-question-and-exclamation-mark [string]
  (str (util/with-question-mark string) "!"))

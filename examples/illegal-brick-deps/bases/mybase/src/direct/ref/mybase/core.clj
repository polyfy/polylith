(ns direct.ref.mybase.core
  (:require [direct.ref.stuff.interface :as stuff]))

(defn say-hi [name]
  (println (stuff/hi-my-friend name)))

(comment
  (say-hi "Buddy")
  #__)

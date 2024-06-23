(ns direct.ref.stuff.interface
  (:require [direct.ref.util.interface :as util]))

(defn hi-my-friend [friend]
  (str "Hi " (util/my-dear-friend friend)))

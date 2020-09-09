(ns polylith.clj.core.validator.core)

(defn has-errors? [messages]
  (-> (filter #(= "error" (:type %)) messages) empty? not))

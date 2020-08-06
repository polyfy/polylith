(ns polylith.clj.core.command.exit-code)

(defn code [cmd {:keys [messages]}]
  (if (= "check" cmd)
    (let [errors (filter #(= "error" (:type %)) messages)]
      (condp = (count errors)
        0 0
        1 (-> errors first :code)
        1))
    0))

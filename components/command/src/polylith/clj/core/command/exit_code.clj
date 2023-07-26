(ns ^:no-doc polylith.clj.core.command.exit-code)

(defn code [cmd {:keys [config-error messages]} test-ok?]
  (if config-error
    1
    (if (= "check" cmd)
      (let [errors (filter #(= "error" (:type %)) messages)]
        (condp = (count errors)
          0 0
          1 (-> errors first :code)
          1))
      (if test-ok?
        0
        1))))

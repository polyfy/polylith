(ns polylith.clj.core.command.test-args)

(def options #{"-all" "-all-bricks" "-env"})

(defn args [arg1 arg2]
  (let [args (set (filter identity [arg1 arg2]))
        env (first (filter #(not (contains? options %)) args))
        option (first (filter #(contains? options %) args))]
    {:env env
     :run-all? (or (= "-all" option)
                   (= "-all-bricks" option))
     :run-env-tests? (or (= "-all" option)
                         (= "-env" option))}))

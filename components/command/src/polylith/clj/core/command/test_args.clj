(ns polylith.clj.core.command.test-args)

(defn args [arg1 arg2]
  (let [args (set (filter identity [arg1 arg2]))
        env (first (filter #(not (contains? #{"-all" "-env"} %)) args))
        all? (contains? args "-all")
        run-env-tests? (contains? args "-env")]
    {:env env
     :run-all? all?
     :run-env-tests? run-env-tests?}))

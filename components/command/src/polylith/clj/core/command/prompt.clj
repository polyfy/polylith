(ns polylith.clj.core.command.prompt
  (:require [clojure.string :as str]
            [polylith.clj.core.user-input.interface :as user-input]))

(defn start-user-prompt [command-executor {:keys [name]}]
  (loop []
    (print (str name "$> "))
    (flush)
    (when-let [line (read-line)]
      (let [{:keys [cmd] :as input} (user-input/extract-params (str/split line #"\s"))]
        (when-not (contains? #{"exit" "quit" "prompt"} cmd)
          (when (not (= "" line))
            (command-executor (assoc input :is-prompt true)))
          (flush)
          (recur))))))

(ns polylith.clj.core.command.prompt
  (:require [clojure.string :as str]
            [polylith.clj.core.user-input.interface :as user-input]))

(defn start-user-prompt [command-executor {:keys [name]}]
  (loop []
    (print (str name "$> "))
    (flush)
    (let [str-input (str/trim (read-line))
          {:keys [cmd] :as input} (user-input/extract-params (str/split str-input #"\s"))]
      (when-not (contains? #{"exit" "quit" "prompt"} cmd)
        (when (not (= "" str-input))
          (command-executor (assoc input :is-prompt true)))
        (flush)
        (recur)))))

(ns polylith.clj.core.command.prompt
  (:require [clojure.string :as str]
            [polylith.clj.core.user-input.interface :as user-input]
            [polylith.clj.core.util.interface.color :as color]))

(defn start-user-prompt [command-executor {:keys [name]} color-mode]
  (loop []
    (print (str name "$> "))
    (flush)
    (when-let [line (read-line)]
      (let [{:keys [cmd] :as input} (user-input/extract-params (str/split line #"\s"))]
        (when-not (contains? #{"exit" "quit" "prompt"} cmd)
          (when (not (= "" line))
            (try
              (command-executor (assoc input :is-prompt true))
              (catch Throwable e
                (println (color/error color-mode (.getMessage e))))))
          (flush)
          (recur))))))

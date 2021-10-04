(ns polylith.clj.core.sh.core
  (:require [clojure.java.shell :as shell]))

(defn execute [args]
  (let [current-project (into {} (System/getenv))
        new-env (dissoc current-project "CLASSPATH")]
    (shell/with-sh-env new-env (apply shell/sh args))))

(defn sh-dont-print-exception [args]
  (let [{:keys [exit out]} (execute args)]
    (when (zero? exit)
      out)))

(defn sh-print-and-throw-if-exception [args]
  (let [{:keys [exit out err]} (execute args)]
    (if (zero? exit)
      out
      (do
        (println out)
        (throw (ex-info (str "Shell Err: " err " Exit code: " exit) {:exit-code exit
                                                                     :error     err}))))))

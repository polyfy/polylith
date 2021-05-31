(ns polylith.clj.core.shell.core
  (:require [clojure.java.shell :as shell]))

(defn sh [args throw-error?]
  (let [current-project (into {} (System/getenv))
        new-env (dissoc current-project "CLASSPATH")
        {:keys [exit out err]} (shell/with-sh-env new-env (apply shell/sh args))]
    (if (= 0 exit)
      out
      (when throw-error?
        ;; Print out the stack trace with the error message
        (println out)
        (throw (ex-info (str "Shell Err: " err " Exit code: " exit) {:exit-code exit
                                                                     :error     err}))))))

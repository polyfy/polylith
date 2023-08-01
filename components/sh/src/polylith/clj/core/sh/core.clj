(ns ^:no-doc polylith.clj.core.sh.core
  (:require [clojure.java.shell :as shell]))

(defn execute [args]
  (let [env (dissoc (into {} (System/getenv))
                    "CLASSPATH")]
    (shell/with-sh-env env (apply shell/sh args))))

(defn sh-dont-print-exception [args]
  (let [{:keys [exit out]} (execute args)]
    (when (zero? exit)
      out)))

(defn sh-print-and-throw-if-exception [args]
  (let [{:keys [exit out err]} (execute args)]
    (if (zero? exit)
      out
      (throw (ex-info (str "Shell Err: " err " Exit code: " exit)
                      {:exit-code exit
                       :error err})))))
(comment
  (shell/sh "git" "rev-parse" "--show-toplevel")
  #__)

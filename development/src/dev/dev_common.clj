(ns dev.dev-common
  (:require [polylith.clj.core.command.interface :as command]
            [polylith.clj.core.user-input.interface :as user-input]))

(defn file [ws-file & args]
  (user-input/extract-arguments (concat ["info" (str "ws-file:" ws-file) "replace:/Users/joakimtengstrand:USER-HOME"]
                                        args)))

(defn dir [ws-dir & args]
  (user-input/extract-arguments (concat ["info" (str "ws-dir:" ws-dir) "replace:/Users/joakimtengstrand:USER-HOME"]
                                        args)))

(defn execute [command ws-dir & args]
  (-> (user-input/extract-arguments (concat [command (str "ws-dir:" ws-dir) "replace:/Users/joakimtengstrand:USER-HOME"]
                                            args))
      (command/execute-command)))

(defn execute-from-file [command ws-file & args]
  (-> (user-input/extract-arguments (concat [command (str "ws-file:" ws-file) "replace:/Users/joakimtengstrand:USER-HOME"]
                                            args))
      (command/execute-command)))

(ns polylith.clj.core.shell.interface
  (:require [polylith.clj.core.shell.core :as core]))

(defn start
  "Starts an interactive shell."
  [command-executor user-input workspace-fn workspace color-mode]
  (core/start command-executor user-input workspace-fn workspace color-mode))

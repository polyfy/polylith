(ns polylith.core.shell.interface
  (:require [polylith.core.shell.core :as core]))

(defn sh [& args]
  (core/sh args))

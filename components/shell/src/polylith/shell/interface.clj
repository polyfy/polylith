(ns polylith.shell.interface
  (:require [polylith.shell.core :as core]))

(defn sh [& args]
  (core/sh args))

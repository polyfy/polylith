(ns polylith.clj.core.shell.interface
  (:require [polylith.clj.core.shell.core :as core]))

(defn sh [& args]
  (core/sh args true))

(defn sh-ignore-exception [& args]
  (core/sh args false))

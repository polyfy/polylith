(ns polylith.clj.core.shell.interface
  (:require [polylith.clj.core.shell.core :as core]))

(defn sh [& args]
  (core/sh-print-and-throw-if-exception args))

(defn sh-with-return [& args]
  (core/execute args))

(defn sh-ignore-exception [& args]
  (core/sh-dont-print-exception args))

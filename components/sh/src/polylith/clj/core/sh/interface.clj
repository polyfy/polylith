(ns ^:no-doc polylith.clj.core.sh.interface
  (:require [polylith.clj.core.sh.core :as core]))

(defn execute [& args]
  (core/sh-print-and-throw-if-exception args))

(defn execute-with-return [& args]
  (core/execute args))

(defn execute-ignore-exception [& args]
  (core/sh-dont-print-exception args))

(ns polylith.core.shell.interfc
  (:require [polylith.core.shell.core :as core]))

(defn sh [& args]
  (core/sh args))

(ns polylith.clj.core.shell.interfc
  (:require [polylith.clj.core.shell.core :as core]))

(defn sh [& args]
  (core/sh args))

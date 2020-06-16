(ns polylith.cmd.interface
  (:require [polylith.cmd.test :as test])
  (:refer-clojure :exclude [compile test]))

(defn test [workspace env]
  (test/test workspace env))

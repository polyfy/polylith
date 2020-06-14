(ns polylith.cmd.interface
  (:require [polylith.cmd.compile :as compile]
            [polylith.cmd.test :as test])
  (:refer-clojure :exclude [compile test]))

(defn compile [workspace env]
  (compile/compile workspace env))

(defn test [workspace env]
  (test/test workspace env))

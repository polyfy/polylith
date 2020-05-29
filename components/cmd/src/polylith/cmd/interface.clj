(ns polylith.cmd.interface
  (:require [polylith.cmd.compile :as compile]
            [polylith.cmd.test :as test])
  (:refer-clojure :exclude [compile test]))

;(defn compile [ws-path deps service-or-env]
;  (compile/compile ws-path deps service-or-env))

;(defn test [ws-path deps service-or-env]
;  (test/test ws-path deps service-or-env))

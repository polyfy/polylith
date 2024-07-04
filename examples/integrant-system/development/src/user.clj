(ns
  ^{:author "Mark Sto"}
  user
  (:require [clojure.tools.logging :as log]
            [integrant.pg-ops.interface :as pg-ops]
            [integrant.system.core :as system]
            [integrant.system.state :as state]))

(defn- get-data-source
  []
  (get-in @state/*state [:system :integrant.system/data-source]))

(comment
  ;; 1. Launch a new system
  (system/launch!)

  ;; 2. Check that it works
  (log/info (pg-ops/query-version (get-data-source)))

  ;; 3. Check restart works
  (state/restart!)

  ;; 4. Shutdown the system
  (system/shutdown!)
  .)

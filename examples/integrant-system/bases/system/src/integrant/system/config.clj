(ns
  ^{:author "Mark Sto"}
  integrant.system.config
  (:require [clojure.tools.logging :as log]
            [integrant.config.interface :as config]
            [integrant.core :as ig]))

(defmethod ig/init-key :integrant.system/config
  [_ _]
  (log/info "Loading configuration")
  (config/load-config))

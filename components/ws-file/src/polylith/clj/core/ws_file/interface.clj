(ns polylith.clj.core.ws-file.interface
  (:require [polylith.clj.core.ws-file.from-disk :as from-disk]))

(defn read-ws-from-file [ws-file user-input]
  (from-disk/read-ws-from-file ws-file user-input))

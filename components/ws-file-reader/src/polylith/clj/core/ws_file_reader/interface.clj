(ns ^:no-doc polylith.clj.core.ws-file-reader.interface
  (:require [polylith.clj.core.ws-file-reader.from-disk :as from-disk]))

(defn read-ws-from-file [ws-file user-input]
  (from-disk/read-ws-from-file ws-file user-input))

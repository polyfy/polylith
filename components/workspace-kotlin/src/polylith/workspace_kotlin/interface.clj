(ns polylith.workspace-kotlin.interface
  (:require [polylith.workspace-kotlin.core :as core]))

(defn workspace-from-disk
  ([ws-path]
   (core/workspace-from-disk ws-path)))
;([ws-path config]
; (core/workspace-from-disk ws-path config)))

(ns polylith.clj.core.z-jocke.api
  (:require [clojure.string :as str]
            [polylith.clj.core.workspace.interfc :as ws]
            [polylith.clj.core.util.interfc :as util]
            [polylith.clj.core.workspace-clj.interfc :as ws-clj]))

; (require '[polylith.clj.core.z-jocke.api :as z])

;(map (juxt :name :interface-deps)
;     (:components (->
;                    "."
;                    ws-clj/workspace-from-disk
;                    ws/enrich-workspace)))

;(->
;  "."
;  ;"../clojure-polylith-realworld-example-app"
;  ws-clj/workspace-from-disk
;  ws/enrich-workspace)

;(def str-workspace (walk/stringify-keys workspace))
;
;
;(class (ws/enrich-workspace-str-keys str-workspace))
;

;(def workspace (->
;                 "."
;                 ;"../clojure-polylith-realworld-example-app"
;                 ws-clj/workspace-from-disk
;                 ws/enrich-workspace))

;(def environments (:environments workspace))
;(def environment (second environments))
;(def interfaces (:interfaces workspace))
;(def components (:components workspace))
;(def ws-bases (:bases workspace))


(ns polylith.clj.core.z-jocke.api
  (:require [clojure.string :as str]
            [polylith.clj.core.workspace.interfc :as ws]
            [polylith.clj.core.change.interfc :as change]
            [polylith.clj.core.util.interfc :as util]
            [polylith.clj.core.workspace-clj.interfc :as ws-clj]))

;(map (juxt :name :interface-deps)
;     (:components (->
;                    "."
;                    ws-clj/workspace-from-disk
;                    ws/enrich-workspace)))

;(->
;  "."
;  ;"../clojure-polylith-realworld-example-app"
;  ws-clj/workspace-from-disk
;  ws/enrich-workspace
;  change/with-changes)

;(def str-workspace (walk/stringify-keys workspace))

;(def workspace (->
;                 "."
;                 ;"../clojure-polylith-realworld-example-app"
;                 ws-clj/workspace-from-disk
;                 ws/enrich-workspace
;                 change/with-changes))

;(require '[polylith.clj.core.z-jocke.api :as z])
;(def workspace z/workspace)

;(def environments (:environments workspace))
;(def components (:components workspace))
;(def ws-bases (:bases workspace))
;(def interfaces (:interfaces workspace))
;(def changes (:changes workspace))

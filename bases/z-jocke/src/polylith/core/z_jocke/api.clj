(ns polylith.core.z-jocke.api
  (:require [clojure.string :as str]
            [polylith.core.workspace.interfc :as ws]
            [polylith.core.util.interfc :as util]
            [polylith.clj.workspace-clj.interfc :as ws-clj]))

;(map (juxt :name :interface-deps)
;     (:bases (->
;               ;"."
;               "../clojure-polylith-realworld-example-app"
;               ws-clojure/workspace-from-disk
;               core/enrich-workspace)))L
;   ;change/with-changes)

;(def workspace (->
;                 "."
;                 ;"../clojure-polylith-realworld-example-app"
;                 ws-clj/workspace-from-disk
;                 ws/enrich-workspace))

;(->
;  "."
;  ;"../clojure-polylith-realworld-example-app"
;  ws-clj/workspace-from-disk
;  ws/enrich-workspace)


;(def environments (:environments workspace))
;
;(def env-names (mapv #(select-keys % [:name :group :test?]) environments))

;
;(:environments (->
;                 "."
;                 ;"../clojure-polylith-realworld-example-app"
;                 ws-clojure/workspace-from-disk
;                 core/enrich-workspace))


;(def components (:components workspace))
;
;(map (juxt :name :interface-deps) components)

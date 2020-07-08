(ns polylith.clj.core.z-jocke.api
  (:require [clojure.string :as str]
            [clojure.walk :as walk]
            [polylith.clj.core.workspace.interfc :as ws]
            [polylith.clj.core.util.interfc :as util]
            [polylith.clj.core.workspace-clj.interfc :as ws-clj]))

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


;(def str-workspace (walk/stringify-keys workspace))
;
;
;(class (ws/enrich-workspace-str-keys str-workspace))
;
;
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

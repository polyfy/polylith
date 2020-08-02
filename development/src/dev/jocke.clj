(ns dev.jocke
  (:require [clojure.string :as str]
            [polylith.clj.core.workspace.interfc :as ws]
            [polylith.clj.core.change.interfc :as change]
            [polylith.clj.core.util.interfc :as util]
            [polylith.clj.core.workspace-clj.interfc :as ws-clj]
            [polylith.clj.core.file.interfc :as file]
            [polylith.clj.core.help.interfc :as help]
            [polylith.clj.core.common.interfc :as common])
  (:refer-clojure :exclude [base]))

(def workspace (->
                 "."
                 ;"/Users/tengstrand/source/poly-example/ws30"
                 ;"../clojure-polylith-realworld-example-app"
                 ws-clj/workspace-from-disk
                 ws/enrich-workspace
                 change/with-changes))

;(require '[dev.jocke :as z])
;(def workspace z/workspace)

(def settings (:settings workspace))
(def environments (:environments workspace))
(def components (:components workspace))
(def bases (:bases workspace))
(def bricks (concat components bases))
(def interfaces (:interfaces workspace))F
(def changes (:changes workspace))

(def environment (common/find-environment "dev" environments))
(def environment (common/find-environment "cli" environments))
(def component (common/find-component "util" components))

(map (juxt :name :alias) environments)

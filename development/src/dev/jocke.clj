(ns dev.jocke
  (:require [clojure.string :as str]
            [polylith.clj.core.workspace.interfc :as ws]
            [polylith.clj.core.change.interfc :as change]
            [polylith.clj.core.util.interfc :as util]
            [polylith.clj.core.path-finder.interfc.extract :as extract]
            [polylith.clj.core.workspace-clj.interfc :as ws-clj]
            [polylith.clj.core.common.interfc :as common]
            [polylith.clj.core.file.interfc :as file]
            [polylith.clj.core.help.interfc :as help]
            [polylith.clj.core.user-input.interfc :as user-input]
            [clojure.set :as set])
  (:refer-clojure :exclude [base]))

;(require '[dev.jocke :as z])
;(def workspace z/workspace)

(defn input [ws-dir]
  (user-input/extract-params [(str "ws-dir:" ws-dir)]))

(def workspace (->
                 (input ".")
                 ;(input "../poly-example/ws50")
                 ;(input "../clojure-polylith-realworld-example-app")
                 ws-clj/workspace-from-disk
                 ws/enrich-workspace
                 change/with-changes))

(:messages workspace)
(:changes workspace)
(:settings workspace)
(:user-input workspace)
(-> workspace :settings :profile->settings)

(def environments (:environments workspace))
(def settings (:settings workspace))
(def interfaces (:interfaces workspace))
(def components (:components workspace))
(def bases (:bases workspace))
(def bricks (vec (concat components bases)))
(def interfaces (:interfaces workspace))
(def changes (:changes workspace))
(def messages (:messages workspace))

(def environment (common/find-environment "dev" environments))
(def component (common/find-component "article" components))
(def base (common/find-base "rest-api" bases))

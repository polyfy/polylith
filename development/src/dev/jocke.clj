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
            [polylith.clj.core.user-input.interfc :as user-input])
  (:refer-clojure :exclude [base]))

(def user-input (user-input/extract-params []))

;(require '[dev.jocke :as z])
;(def workspace z/workspace)

(def workspace (->
                 ;"."
                 "../poly-example/ws50"
                 ;"../poly-example/ws52"
                 ;"../clojure-polylith-realworld-example-app"
                 ws-clj/workspace-from-disk
                 (ws/enrich-workspace user-input)
                 change/with-changes))

(:messages workspace)
(:changes workspace)
(:settings workspace)
(:user-input workspace)

(slurp "../poly-example/ws50/deps.edn")

(def environments (:environments workspace))
(def environment (common/find-environment "development" environments))
(def environment (common/find-environment "invoice" environments))

(map :name environments)
(map :active? environments)

(:lib-deps environment)
(:test-lib-deps environment)

(def settings (:settings workspace))
(def components (:components workspace))
(def bases (:bases workspace))
(def bricks (concat components bases))
(def interfaces (:interfaces workspace))
(def changes (:changes workspace))
(def messages (:messages workspace))

(map :name environments)

(def environment (common/find-environment "cli" environments))
(def environment (common/find-environment "development" environments))

(def component (common/find-component "command" components))
(def base (common/find-base "rest-api" bases))

(def config (read-string (slurp "/Users/tengstrand/source/poly-example/ws35/environments/core/deps.edn")))

(ns dev.jocke
  (:require [clojure.set :as set]
            [clojure.string :as str]
            [polylith.clj.core.builder.core :as builder-core]
            [polylith.clj.core.workspace.interface :as ws]
            [polylith.clj.core.change.interface :as change]
            [polylith.clj.core.util.interface :as util]
            [polylith.clj.core.path-finder.interface.extract :as extract]
            [polylith.clj.core.workspace-clj.interface :as ws-clj]
            [polylith.clj.core.common.interface :as common]
            [polylith.clj.core.file.interface :as file]
            [polylith.clj.core.help.interface :as help]
            [polylith.clj.core.builder.interface :as builder]
            [polylith.clj.core.user-input.interface :as user-input]
            [uberdeps.api :as uberdeps])
  (:refer-clojure :exclude [base]))

;(require '[dev.jocke :as z])
;(def workspace z/workspace)

(defn dir [ws-dir]
  (user-input/extract-params [(str "ws-dir:" ws-dir)]))

(def workspace (->
                 (dir ".")
                 ;(dir "../poly-example/ws50")
                 ;(dir "../clojure-polylith-realworld-example-app")
                 ws-clj/workspace-from-disk))
                 ;ws/enrich-workspace
                 ;change/with-changes))

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
(def environment (common/find-environment "poly" environments))
(def environment (common/find-environment "migrator" environments))
(def component (common/find-component "common" components))
(def component (common/find-component "article" components))
(def base (common/find-base "poly-cli" bases))

(def input (user-input/extract-params ["build" "env:poly" ":aot"]))
(def input (user-input/extract-params ["build" "env:dev" ":aot"]))

(builder/build workspace input "none")

;(builder-core/find-main-class bases environment)
;(:base-names environment)

(uberdeps/)
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

(def user-input (common/user-input []))

(def workspace (->
                 ;"."
                 "../poly-example/ws50"
                 ;"../clojure-polylith-realworld-example-app"
                 ws-clj/workspace-from-disk
                 (ws/enrich-workspace user-input)
                 (change/with-changes user-input)))

(:messages workspace)
(:changes workspace)

(:settings workspace)
;(-> workspace :settings :active-dev-profiles)


(def components (:components workspace))
(def environments (:environments workspace))
(def environment (common/find-environment "development" environments))
(:component-names environment)

(-> workspace :changes :user-input)


;(-> workspace :settings :active-dev-profiles)

;(require '[dev.jocke :as z])
;(def workspace z/workspace)

(def settings (:settings workspace))
(def bases (:bases workspace))
(def bricks (concat components bases))
(def interfaces (:interfaces workspace))
(def changes (:changes workspace))
(def messages (:messages workspace))

(:active-dev-profiles settings)

(-> settings :profile->settings :default :paths)

(map (juxt :name :active?) environments)




(def environment (common/find-environment "cli" environments))
(def component (common/find-component "command" components))
(def base (common/find-base "rest-api" bases))

(:dev? environment)



(:lib-deps component)
(:lib-deps base)

(map :name bases)

(:lib-deps component)
(:lib-deps base)

(:lib-deps environment)

(common/find-environment "core" environments)


(map (juxt :alias :lib->deps) environments)

(def config (read-string (slurp "/Users/tengstrand/source/poly-example/ws35/environments/core/deps.edn")))

(-> config :paths)
(-> config :aliases :test :extra-paths)
(read-string (slurp "/Users/tengstrand/source/poly-example/ws34/components/abc/src/abc/interface.clj"))

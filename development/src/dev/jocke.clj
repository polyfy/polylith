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
                 ;"."
                 "/Users/tengstrand/source/poly-example/ws35"
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
(def interfaces (:interfaces workspace))
(def changes (:changes workspace))

(def environment (common/find-environment "dev" environments))
(def environment (common/find-environment "cli" environments))
(def component (common/find-component "user" components))

(common/find-environment "core" environments)


(map (juxt :alias :lib-deps) environments)

(def config (read-string (slurp "/Users/tengstrand/source/poly-example/ws35/environments/core/deps.edn")))

(-> config :paths)
(-> config :aliases :test :extra-paths)
(read-string (slurp "/Users/tengstrand/source/poly-example/ws34/components/abc/src/abc/interface.clj"))

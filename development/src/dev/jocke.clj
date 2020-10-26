(ns dev.jocke
  (:require [clojure.set :as set]
            [clojure.string :as str]
            [polylith.clj.core.api.interface :as api]
            [polylith.clj.core.workspace.interface :as ws]
            [polylith.clj.core.change.interface :as change]
            [polylith.clj.core.command.interface :as command]
            [polylith.clj.core.git.interface :as git]
            [polylith.clj.core.util.interface :as util]
            [polylith.clj.core.path-finder.interface.extract :as extract]
            [polylith.clj.core.workspace-clj.interface :as ws-clj]
            [polylith.clj.core.common.interface :as common]
            [polylith.clj.core.file.interface :as file]
            [polylith.clj.core.help.interface :as help]
            [polylith.clj.core.user-input.interface :as user-input])
  (:refer-clojure :exclude [base]))

;(require '[dev.jocke :as z])
;(def workspace z/workspace)

(defn ws-from-file [filename]
  (let [input (user-input/extract-params ["ws" (str "ws-file:" filename)])]
    (command/read-workspace "." input)))

(defn dir [ws-dir]
  (user-input/extract-params ["info" (str "ws-dir:" ws-dir)]))

(def workspace (->
                 ;(dir ".")
                 (dir "../poly-example/ws02")
                 ;(dir "../clojure-polylith-realworld-example-app")
                 ws-clj/workspace-from-disk
                 ws/enrich-workspace))
                 ;change/with-changes))

(keys workspace)

;(command/execute-command (user-input/extract-params ["info"]))
;(command/execute-command (user-input/extract-params ["test"]))

;(command/execute-command (user-input/extract-params ["info"]))

(keys (:lib-deps (common/find-project "dev" (:projects workspace))))

(-> workspace :projects first :name)

(map :alias (:projects workspace))

(:projects workspace)
(:messages workspace)
(:changes workspace)
(:settings workspace)
(:user-input workspace)
(-> workspace :settings :profile-to-settings)

(def projects (:projects workspace))
(def settings (:settings workspace))
(def interfaces (:interfaces workspace))
(def components (:components workspace))
(def bases (:bases workspace))
(def bricks (vec (concat components bases)))
(def interfaces (:interfaces workspace))
(def changes (:changes workspace))
(def messages (:messages workspace))

(map :name projects)

(def project (common/find-project "dev" projects))
(def project (common/find-project "invoice" projects))
(def project (common/find-project "poly-migrator" projects))
(def component (common/find-component "user" components))
(def component (common/find-component "article" components))
(def base (common/find-base "poly-cli" bases))

(def changed-components (-> workspace :changes :changed-components))
(def changed-bases (-> workspace :changes :changed-bases))
(def changed-bricks (set (concat changed-components changed-bases)))
(def brick-changed? (-> (set/intersection bricks changed-bricks)
                        empty? not))

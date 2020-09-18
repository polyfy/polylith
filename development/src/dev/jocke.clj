(ns dev.jocke
  (:require [clojure.set :as set]
            [clojure.string :as str]
            [polylith.clj.core.api.interface :as api]
            [polylith.clj.core.workspace.interface :as ws]
            [polylith.clj.core.change.interface :as change]
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

(defn dir [ws-dir]
  (user-input/extract-params ["info" (str "ws-dir:" ws-dir)]))

(def workspace (->
                 (dir ".")
                 ;(dir "../poly-example/ws50")
                 ;(dir "../clojure-polylith-realworld-example-app")
                 ws-clj/workspace-from-disk
                 ws/enrich-workspace
                 change/with-last-stable-changes))
                 ;change/with-last-build-changes))

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

(map :name environments)

(def env-bricks (set (concat component-names base-names)))
(def changed-components (-> workspace :changes :changed-components))
(def changed-bases (-> workspace :changes :changed-bases))
(def changed-bricks (set (concat changed-components changed-bases)))
(def brick-changed? (-> (set/intersection bricks changed-bricks)
                        empty? not))

(defn env-changed? [{:keys [name component-names base-names]}
                    {:keys [changed-components changed-bases changed-environments]}]
  (let [bricks (set (concat component-names base-names))
        changed-bricks (set (concat changed-components changed-bases))
        brick-changed? (-> (set/intersection bricks changed-bricks)
                           empty? not)
        environment-changed? (contains? (set changed-environments) name)]
    (or brick-changed? environment-changed?)))

(env-changed? environment changes)

(def name (:name environment))
(def component-names (:component-names environment))
(def base-names (:base-names environment))





; last-stable 035cc0989598a5bb86f556f8e542adc2dad2eee6
(-> workspace :changes :sha1)
; last-release 80f3c3a06b276e0fe5abab449a89593a113b45d6
(-> workspace :changes)

(git/diff "." "035cc0989598a5bb86f556f8e542adc2dad2eee6" nil)
(git/diff "." "80f3c3a06b276e0fe5abab449a89593a113b45d6" nil)



(api/changed-environments)

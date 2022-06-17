(ns dev.jocke
  (:require [dev.dev-common :as dev-common]
            [clojure.set :as set]
            [clojure.string :as str]
            [clojure.pprint :as pp]
            [clojure.tools.deps.alpha :as tools-deps]
            [polylith.clj.core.api.interface :as api]
            [polylith.clj.core.workspace.interface :as ws]
            [polylith.clj.core.change.interface :as change]
            [polylith.clj.core.command.interface :as command]
            [polylith.clj.core.git.interface :as git]
            [polylith.clj.core.util.interface :as util]
            [polylith.clj.core.command.info :as info]
            [polylith.clj.core.path-finder.interface.extract :as extract]
            [polylith.clj.core.workspace-clj.interface :as ws-clj]
            [polylith.clj.core.common.interface :as common]
            [polylith.clj.core.file.interface :as file]
            [polylith.clj.core.help.interface :as help]
            [clojure.tools.deps.alpha.util.maven :as mvn]
            [polylith.clj.core.user-input.interface :as user-input])
  (:refer-clojure :exclude [base]))

;(require '[dev.jocke :as z])
;(def workspace z/workspace)

(def workspace (-> (dev-common/dir ".")
                   ;(dev-common/dir "examples/doc-example")
                   ;(dev-common/dir "examples/local-dep")
                   ;(dev-common/dir "examples/local-dep-old-format")
                   ;(dev-common/dir "../poly-example/ws02")
                   ;(dev-common/dir "../clojure-polylith-realworld-example-app")
                   ;(dev-common/dir "../sandbox/ws38")
                   ;(dev-common/dir "../sandbox/ws03")
                   ;(dev-common/dir "../usermanager-example")
                   ws-clj/workspace-from-disk
                   ws/enrich-workspace
                   change/with-changes))


;(spit "development/data/workspace.edn" (with-out-str (pp/pprint workspace)))

;(info/info workspace nil)

;(command/execute-command (user-input/extract-params ["info" ":all" "project:poly" "brick:-"]))
;(command/execute-command (user-input/extract-params ["ws" "get:changes:project-to-projects-to-test:poly" ":all" "project:poly" "brick:-"]))
;(command/execute-command (user-input/extract-params ["test" ":all" "project:poly" "brick:-"]))

;(command/execute-command (user-input/extract-params ["test"]))

(:projects workspace)
(:messages workspace)
(:changes workspace)
(:settings workspace)
(:user-input workspace)

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
(def project (common/find-project "invoicing" projects))
(def project (common/find-project "poly-migrator" projects))
(def project (common/find-project "um" projects))
(def component (common/find-component "user" components))
(def component (common/find-component "util" components))
(def component (common/find-component "article" components))
(def component (common/find-component "schema" components))
(def component (common/find-component "without-src" components))
(def base (common/find-base "poly-cli" bases))

(def changed-components (-> workspace :changes :changed-components))
(def changed-bases (-> workspace :changes :changed-bases))
(def changed-bricks (set (concat changed-components changed-bases)))
(def brick-changed? (-> (set/intersection bricks changed-bricks)
                        empty? not))

(ns dev.jocke
  (:require [dev.dev-common :as dev-common]
            [clojure.set :as set]
            [clojure.string :as str]
            [clojure.pprint :as pp]
            [clojure.tools.deps :as tools-deps]
            [polylith.clj.core.api.interface :as api]
            [polylith.clj.core.sh.interface :as sh]
            [polylith.clj.core.workspace.interface :as workspace]
            [polylith.clj.core.change.interface :as change]
            [polylith.clj.core.command.interface :as command]
            [polylith.clj.core.git.interface :as git]
            [polylith.clj.core.util.interface :as util]
            [polylith.clj.core.command.info :as info]
            [polylith.clj.core.validator.m207-unnecessary-components-in-project :as validator207]
            [polylith.clj.core.path-finder.interface.extract :as extract]
            [polylith.clj.core.ws-explorer.interface :as ws-explorer]
            [polylith.clj.core.common.interface :as common]
            [polylith.clj.core.file.interface :as file]
            [polylith.clj.core.help.interface :as help]
            [clojure.tools.deps.util.maven :as mvn]
            [polylith.clj.core.user-input.interface :as user-input])
  (:refer-clojure :exclude [base]))

; clojure -A:dev:test -P

;; Execute commands
;(dev-common/execute "info" ".")
;(dev-common/execute-from-file "info" "profiles.edn" "+")

;(dev-common/execute "libs" ".")
;(dev-common/execute "info" "../sandbox/polylith218")
;(dev-common/execute "test" "." ":all")

;(def workspace (-> (dev-common/ws-from-file "info" "../sandbox/polylith218/ws.edn")))


;(println (sh/execute "java" "-jar" "projects/poly/target/poly.jar" "check"))

(def workspace (-> ;(dev-common/dir ".")
                   ;(dev-common/dir "examples/doc-example")
                   ;(dev-common/dir "examples/for-test")
                   ;(dev-common/dir "examples/profiles" "+")
                   ;(dev-common/dir "examples/illegal-brick-deps")
                   ;(dev-common/dir "examples/test-runners" "with:default-test-runner")
                   ;(dev-common/dir "examples/local-dep" "changed-files:components/util/")
                   ;(dev-common/dir "examples/test-runners" "skip:external-inherit-from-global:multiple-test-runners:development")
                   ;(dev-common/dir "/var/folders/_0/7sl6982d6l7bzdlypmk308kw0000gn/T/polylith-example-2024-01-14-114017/ws/example" ":all" ":dev") ;"skip:dev:user-s")
                   ;(dev-common/dir "examples/profiles" "changed-files:bases/base2/src/se/example/base2/core.clj")
                   ;(dev-common/dir "examples/local-dep-old-format")
                   ;(dev-common/dir "../poly-example/ws04")
                   ;(dev-common/dir "../clojure-polylith-realworld-example-app")
                   ;(dev-common/dir "../sandbox/polylith218")
                   ;(dev-common/dir "../usermanager-example")
                   workspace/workspace))


(:messages workspace)
(:user-input workspace)


(-> workspace :workspaces)

(-> workspace :projects second :lib-deps)
(-> workspace :projects second :name)

(:profiles workspace)

;;(map :name (:projects workspace))


;projects:development:lib-dep

(mapv :alias (-> workspace :workspaces))
(keys workspace)



(-> workspace :settings :test)

(-> workspace :configs :workspace :test)
(-> workspace :settings :test)

(:project-lib-deps workspace)
(-> workspace :projects first :project-lib-deps)

(keys workspace)

(:workspaces workspace)

(:workspaces workspace)


(:messages workspace)
(:configs workspace)
(:projects workspace)
(:changes workspace)
(:settings workspace)
(:user-input workspace)

(def projects (:projects workspace))
(def configs (:configs workspace))
(def settings (:settings workspace))
(def interfaces (:interfaces workspace))
(def components (:components workspace))
(def bases (:bases workspace))
(def bricks (vec (concat components bases)))
(def interfaces (:interfaces workspace))
(def changes (:changes workspace))
(def messages (:messages workspace))

(def project (common/find-project "dev" projects))
(def project (common/find-project "poly" projects))
(def component (common/find-component "util" components))
(def base (common/find-base "poly-cli" bases))

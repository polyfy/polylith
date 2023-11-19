(ns dev.jocke
  (:require [dev.dev-common :as dev-common]
            [clojure.set :as set]
            [clojure.string :as str]
            [clojure.pprint :as pp]
            [clojure.tools.deps :as tools-deps]
            [polylith.clj.core.api.interface :as api]
            [polylith.clj.core.workspace.interface :as ws]
            [polylith.clj.core.change.interface :as change]
            [polylith.clj.core.command.interface :as command]
            [polylith.clj.core.git.interface :as git]
            [polylith.clj.core.util.interface :as util]
            [polylith.clj.core.command.info :as info]
            [polylith.clj.core.validator.m207-unnecessary-components-in-project :as validator207]
            [polylith.clj.core.path-finder.interface.extract :as extract]
            [polylith.clj.core.workspace-clj.interface :as ws-clj]
            [polylith.clj.core.ws-explorer.interface :as ws-explorer]
            [polylith.clj.core.common.interface :as common]
            [polylith.clj.core.file.interface :as file]
            [polylith.clj.core.help.interface :as help]
            [clojure.tools.deps.util.maven :as mvn]
            [polylith.clj.core.user-input.interface :as user-input])
  (:refer-clojure :exclude [base]))

; clojure -A:dev:test -P



(def workspace (-> (dev-common/dir ".")
                   ;(dev-common/dir "examples/doc-example")
                   ;(dev-common/dir "examples/for-test")
                   ;(dev-common/dir "examples/local-dep")
                   ;(dev-common/dir "examples/local-dep-old-format")
                   ;(dev-common/dir "../poly-example/ws02")
                   ;(dev-common/dir "../clojure-polylith-realworld-example-app")
                   ;(dev-common/dir "../sandbox/ws35")
                   ;(dev-common/dir "../sandbox/ws03")
                   ;(dev-common/dir "../usermanager-example")
                   ws-clj/workspace-from-disk
                   ws/enrich-workspace
                   change/with-changes))


;(ws-explorer/ws workspace nil nil "dark")

;(spit "development/data/workspace.edn" (with-out-str (pp/pprint workspace)))

;(info/info workspace nil)

;(command/execute-command (user-input/extract-arguments ["libs" ":outdated"]))
;(command/execute-command (user-input/extract-arguments ["info" ":all" "project:poly" "brick:-"]))
;(command/execute-command (user-input/extract-arguments ["ws" "get:changes:project-to-projects-to-test:poly" ":all" "project:poly" "brick:-"]))
;(command/execute-command (user-input/extract-arguments ["test" ":all" "project:poly" "brick:-"]))

;(command/execute-command (user-input/extract-arguments ["test"]))

;(validator207/warnings (-> workspace :settings) (:projects workspace) true "dark")

(:messages workspace)
(:configs workspace)
(:projects workspace)
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
(def project (common/find-project "api" projects))
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

;;------------------------------

;; polylith.edn

{:workspaces {:polylith {:dir "." :main true}
              :realworld {:dir "../clojure-polylith-realworld-example-app" :alias "r"}
              :doc-example {:dir "examples/doc-example"}
              :for-test {:dir "examples/doc-for-test"}
              :illegal-configs {:dir "examples/illegal-configs"}
              :local-dep {:dir "examples/local-dep"}
              :local-dep-old-format {:dir "examples/local-dep-old-format"}
              :profiles {:dir "examples/profiles"}}}

;; - alla workspaces ligger i filen polylith.edn:
;;   - alias specas i respektive workspace, men kan överridas med :alias.
;;   - det workspace som har :dir satt till "." blir main, men kan överridas med :main true.
;;   - är inget workspace satt till main, så får man ett error.
;;   - läser man in gamla repon som bara har ett workspace, så kommer det sättas till main.
;;
;; - ett workspace ska kunna innehålla både clj och cljs kod.
;;   - vissa låter i stort sett alla komponenter bestå av cljc-kod och har sedan olika
;;     projekt för clj och cljs för de olika artifakterna.
;;   - om vi ska stödja Electric på ett bra sätt, så är de mixade (tror jag).
;; - när man kör ett kommando så ska alltid endast en av dem vara aktiv/vald.
;;   - detta kan anges när man kör ett kommando, t.ex. "poly info :cljs"
;;   - ett shell kan startas som :clj eller :cljs, t.ex. "poly :clj" eller "poly :cljs".
;;   - inne i ett shell så kan man byta med t.ex "type:cljs".
;; - varje project.edn ska innehålla :type + :alias.
;    - om type inte är specificerad, så sätts :type till "clj" som default.
;; - det ska gå att blanda clj och cljs i samma workspace:
;;   - det ska gå att ha olika top-namespace för clj och cljs:
;;     - :top-namespace {:clj "xxx", :cljs "yyy"}
;; - det ska gå att ha olika interface-ns:
;    - :interface-ns {:clj "interface", :cljs "ifc"}
;;


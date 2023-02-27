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
            [polylith.clj.core.common.interface :as common]
            [polylith.clj.core.file.interface :as file]
            [polylith.clj.core.help.interface :as help]
            [clojure.tools.deps.util.maven :as mvn]
            [polylith.clj.core.user-input.interface :as user-input])
  (:refer-clojure :exclude [base]))

;(require '[dev.jocke :as z])
;(def workspace z/workspace)

(def workspace (-> (dev-common/dir ".")
                   ;(dev-common/dir "examples/doc-example")
                   ;(dev-common/dir "examples/for-test")
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

(validator207/warnings (-> workspace :settings) (:projects workspace) true "dark")

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




;-------------------------------------

FAIL in (libs) (workspace_test.clj:67)
expected: (= ["                                                                                                  w   " "                                                                                                  o   " "                                                                                                  r  w" "                                                                                                  k  s" "                                                                                               v  s  -" "                                                                                      m        a  p  e" "                                                                                      i        l  a  x" "                                                                                      g        i  c  p" "                                                                                      r  s     d  e  l" "                                                                                d  f  a  h     a  -  o" "                                                                                e  i  t  e  t  t  c  r" "                                                                                p  l  o  l  a  o  l  e" "  library                           version    type      KB   api  poly   dev   s  e  r  l  p  r  j  r" "  ---------------------------------------------------------   ---------   ---   ----------------------" "  clj-commons/fs                    1.6.310    maven     12    x    x      x    .  x  .  .  .  .  .  ." "  djblue/portal                     0.35.1     maven  1,790    -    x      x    .  .  .  .  x  .  .  ." "  io.github.seancorfield/build-clj  9c9f078    git       42    -    -      x    .  .  .  .  .  .  .  ." "  metosin/malli                     0.10.1     maven     81    x    x      x    .  .  .  .  .  x  .  ." "  mount/mount                       0.1.17     maven      0    -    -      x    .  .  .  .  .  .  .  ." "  mvxcvi/puget                      1.3.2      maven     15    x    x      -    .  .  .  .  .  .  .  x" "  mvxcvi/puget                      1.3.4      maven      0    -    -      x    .  .  .  .  .  .  .  ." "  org.clojure/clojure               1.11.1     maven  4,008    x    x      x    .  .  .  .  .  .  .  ." "  org.clojure/tools.deps            0.16.1281  maven     57    x    x      x    x  x  .  .  .  .  x  ." "  org.jline/jline                   3.22.0     maven  1,035    -    x      x    .  .  .  x  .  .  .  ." "  org.slf4j/slf4j-nop               2.0.6      maven      3    -    x      x    .  .  .  .  .  .  .  ." "  rewrite-clj/rewrite-clj           1.1.46     maven     72    -    -      x    .  .  .  .  .  .  .  ." "  slipset/deps-deploy               0.2.0      maven      7    -    -      x    .  .  .  .  .  .  .  ." "  zprint/zprint                     1.2.5      maven    195    -    x      x    .  .  x  .  .  .  .  ."] (libs/table (workspace) false))
actual: (not (=))


(def libs ["                                                                              t"
           "                                                                              e"
           "                                                                              s"
           "                                                                              t"
           "                                                                              -"
           "                                                                              h"
           "                                                                              e"
           "                                                                              l"
           "                                                                              p"
           "                                                                              e"
           "                                                                              r"
           "  library                 version    type      KB   s   dev  default  extra   1"
           "  -----------------------------------------------   -   -------------------   -"
           "  clj-commons/fs          1.6.310    maven     12   -    -      x       -     ."
           "  metosin/malli           0.5.0      maven     42   t    x      -       -     x"
           "  org.clojure/clojure     1.10.1     maven  3,816   x    x      -       -     ."
           "  org.clojure/tools.deps  0.16.1264  maven     58   x    x      -       -     ."])

(=
  ["                                                                                                  w   " "                                                                                                  o   " "                                                                                                  r  w" "                                                                                                  k  s" "                                                                                               v  s  -" "                                                                                      m        a  p  e" "                                                                                      i        l  a  x" "                                                                                      g        i  c  p" "                                                                                      r  s     d  e  l" "                                                                                d  f  a  h     a  -  o" "                                                                                e  i  t  e  t  t  c  r" "                                                                                p  l  o  l  a  o  l  e" "  library                           version    type      KB   api  poly   dev   s  e  r  l  p  r  j  r" "  ---------------------------------------------------------   ---------   ---   ----------------------" "  clj-commons/fs                    1.6.310    maven     12    x    x      x    .  x  .  .  .  .  .  ." "  djblue/portal                     0.35.1     maven  1,790    -    x      x    .  .  .  .  x  .  .  ." "  io.github.seancorfield/build-clj  9c9f078    git       42    -    -      x    .  .  .  .  .  .  .  ." "  metosin/malli                     0.10.1     maven     81    x    x      x    .  .  .  .  .  x  .  ." "  mount/mount                       0.1.17     maven      0    -    -      x    .  .  .  .  .  .  .  ." "  mvxcvi/puget                      1.3.2      maven     15    x    x      -    .  .  .  .  .  .  .  x" "  mvxcvi/puget                      1.3.4      maven      0    -    -      x    .  .  .  .  .  .  .  ." "  org.clojure/clojure               1.11.1     maven  4,008    x    x      x    .  .  .  .  .  .  .  ." "  org.clojure/tools.deps            0.16.1281  maven     57    x    x      x    x  x  .  .  .  .  x  ." "  org.jline/jline                   3.22.0     maven  1,035    -    x      x    .  .  .  x  .  .  .  ." "  org.slf4j/slf4j-nop               2.0.6      maven      3    -    x      x    .  .  .  .  .  .  .  ." "  rewrite-clj/rewrite-clj           1.1.46     maven     72    -    -      x    .  .  .  .  .  .  .  ." "  slipset/deps-deploy               0.2.0      maven      7    -    -      x    .  .  .  .  .  .  .  ." "  zprint/zprint                     1.2.5      maven    195    -    x      x    .  .  x  .  .  .  .  ."]
  ["                                                                                                  w   " "                                                                                                  o   " "                                                                                                  r  w" "                                                                                                  k  s" "                                                                                               v  s  -" "                                                                                      m        a  p  e" "                                                                                      i        l  a  x" "                                                                                      g        i  c  p" "                                                                                      r  s     d  e  l" "                                                                                d  f  a  h     a  -  o" "                                                                                e  i  t  e  t  t  c  r" "                                                                                p  l  o  l  a  o  l  e" "  library                           version    type      KB   api  poly   dev   s  e  r  l  p  r  j  r" "  ---------------------------------------------------------   ---------   ---   ----------------------" "  clj-commons/fs                    1.6.310    maven     12    x    x      x    .  x  .  .  .  .  .  ." "  djblue/portal                     0.35.1     maven  1,790    -    x      x    .  .  .  .  x  .  .  ." "  io.github.seancorfield/build-clj  9c9f078    git       42    -    -      x    .  .  .  .  .  .  .  ." "  metosin/malli                     0.10.1     maven     81    x    x      x    .  .  .  .  .  x  .  ." "  mount/mount                       0.1.17     maven      8    -    -      x    .  .  .  .  .  .  .  ." "  mvxcvi/puget                      1.3.2      maven     15    x    x      -    .  .  .  .  .  .  .  x" "  mvxcvi/puget                      1.3.4      maven     15    -    -      x    .  .  .  .  .  .  .  ." "  org.clojure/clojure               1.11.1     maven  4,008    x    x      x    .  .  .  .  .  .  .  ." "  org.clojure/tools.deps            0.16.1281  maven     57    x    x      x    x  x  .  .  .  .  x  ." "  org.jline/jline                   3.22.0     maven  1,035    -    x      x    .  .  .  x  .  .  .  ." "  org.slf4j/slf4j-nop               2.0.6      maven      3    -    x      x    .  .  .  .  .  .  .  ." "  rewrite-clj/rewrite-clj           1.1.46     maven     72    -    -      x    .  .  .  .  .  .  .  ." "  slipset/deps-deploy               0.2.0      maven      7    -    -      x    .  .  .  .  .  .  .  ." "  zprint/zprint                     1.2.5      maven    195    -    x      x    .  .  x  .  .  .  .  ."])

(=
  ["                                                                                                  w   " "                                                                                                  o   " "                                                                                                  r  w" "                                                                                                  k  s" "                                                                                               v  s  -" "                                                                                      m        a  p  e" "                                                                                      i        l  a  x" "                                                                                      g        i  c  p" "                                                                                      r  s     d  e  l" "                                                                                d  f  a  h     a  -  o" "                                                                                e  i  t  e  t  t  c  r" "                                                                                p  l  o  l  a  o  l  e" "  library                           version    type      KB   api  poly   dev   s  e  r  l  p  r  j  r" "  ---------------------------------------------------------   ---------   ---   ----------------------" "  clj-commons/fs                    1.6.310    maven     12    x    x      x    .  x  .  .  .  .  .  ." "  djblue/portal                     0.35.1     maven  1,790    -    x      x    .  .  .  .  x  .  .  ." "  io.github.seancorfield/build-clj  9c9f078    git       42    -    -      x    .  .  .  .  .  .  .  ." "  metosin/malli                     0.10.1     maven     81    x    x      x    .  .  .  .  .  x  .  ." "  mount/mount                       0.1.17     maven      0    -    -      x    .  .  .  .  .  .  .  ." "  mvxcvi/puget                      1.3.2      maven     15    x    x      -    .  .  .  .  .  .  .  x" "  mvxcvi/puget                      1.3.4      maven      0    -    -      x    .  .  .  .  .  .  .  ." "  org.clojure/clojure               1.11.1     maven  4,008    x    x      x    .  .  .  .  .  .  .  ." "  org.clojure/tools.deps            0.16.1281  maven     57    x    x      x    x  x  .  .  .  .  x  ." "  org.jline/jline                   3.22.0     maven  1,035    -    x      x    .  .  .  x  .  .  .  ." "  org.slf4j/slf4j-nop               2.0.6      maven      3    -    x      x    .  .  .  .  .  .  .  ." "  rewrite-clj/rewrite-clj           1.1.46     maven     72    -    -      x    .  .  .  .  .  .  .  ." "  slipset/deps-deploy               0.2.0      maven      7    -    -      x    .  .  .  .  .  .  .  ." "  zprint/zprint                     1.2.5      maven    195    -    x      x    .  .  x  .  .  .  .  ."]
  libs)














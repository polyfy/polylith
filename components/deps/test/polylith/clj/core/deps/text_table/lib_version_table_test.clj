(ns polylith.clj.core.deps.text-table.lib-version-table-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.deps.text-table.lib-version-table :as lib-version-table]
            [polylith.clj.core.util.interfc.color :as color]))

(def workspace {:settings {:color-mode color/none
                           :profile->settings {"default" {:lib-deps {}}
                                               "admin" {:lib-deps {"zprint" {:mvn/version "0.4.15"}}}}}
                :environments [{:alias "core"
                                :lib-deps {"org.clojure/clojure" {:mvn/version "1.10.2"}
                                           "org.clojure/tools.deps.alpha" {:mvn/version "0.8.695"}}}
                               {:alias "inv"
                                :lib-deps {"org.clojure/clojure" {:mvn/version "1.10.1"}
                                           "org.clojure/tools.deps.alpha" {:mvn/version "0.8.695"}}}
                               {:alias "dev"
                                :lib-deps {"org.clojure/clojure" {:mvn/version "1.10.1"}
                                           "org.clojure/tools.deps.alpha" {:mvn/version "0.8.695"}}}]})

(deftest table--the-workspace-libraries--return-table-with-environments-and-profiles
  (is (= ["  library                       version   core  inv   dev  default  admin"
          "  -------------------------------------   ---------   -------------------"
          "  org.clojure/clojure           1.10.1     -     x     x      -       -  "
          "  org.clojure/clojure           1.10.2     x     -     -      -       -  "
          "  org.clojure/tools.deps.alpha  0.8.695    x     x     x      -       -  "
          "  zprint                        0.4.15     -     -     -      -       x  "]
         (lib-version-table/table workspace))))

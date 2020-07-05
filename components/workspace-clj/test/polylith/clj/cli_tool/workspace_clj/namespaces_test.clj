(ns polylith.clj.cli-tool.workspace-clj.namespaces-test
  (:require [clojure.test :refer :all]
            [polylith.clj.cli-tool.workspace-clj.namespace :as namespace]))

(def ns-paths ["polylith/core/util/str.clj"
               "polylith/core/util/interfc.clj"
               "polylith/core/util/core.clj"
               "polylith/core/util/interfc/str.clj"
               "polylith/core/util/interfc/color.clj"
               "polylith/core/util/interfc"
               "polylith/core/util"
               "polylith/core"
               "polylith"])

(deftest top-namespace--with-one-matching-top-namespace--returns-that-namespaces
  (is (= ["polylith/core/"]
         (namespace/top-brick-src-dirs ns-paths ["polylith/core/"
                                                 "polylith/cli/"]))))

(deftest top-namespace--with-two-matching-top-namespaces--returns-both-namespaces
  (is (= ["polylith/core/"
          "polylith/core/util/"]
         (namespace/top-brick-src-dirs ns-paths ["polylith/core/"
                                                 "polylith/core/util/"]))))

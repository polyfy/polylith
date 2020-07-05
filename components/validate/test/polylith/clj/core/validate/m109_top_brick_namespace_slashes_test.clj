(ns polylith.clj.core.validate.m109-top-brick-namespace-slashes-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.util.interfc.color :as color]
            [polylith.clj.core.validate.m109-top-brick-namespace-clashes :as clashes]))

(deftest errors--component-without-matching-namespace--returns-error
  (is (= [{:code 109
           :type "error"
           :message "No matching top namespace was found for util. Valid namespaces are: polylith.core"
           :colorized-message "No matching top namespace was found for util. Valid namespaces are: polylith.core"
           :top-namespaces []
           :components ["util"]}]
         (clashes/errors [{:name "util"
                           :type "component"
                           :top-namespaces []}]
                         []
                         ["polylith.core"]
                         color/none))))

(deftest errors--component-with-clashing-namespaces--returns-error
  (is (= [{:code 109
           :type "error"
           :message "Clashing top namespaces was found for util: polylith.core, polylith.core.util"
           :colorized-message "Clashing top namespaces was found for util: polylith.core, polylith.core.util"
           :top-namespaces ["polylith.core" "polylith.core.util"]
           :components ["util"]}]
         (clashes/errors [{:name "util"
                           :type "component"
                           :top-namespaces ["polylith.core"
                                            "polylith.core.util"]}]
                         []
                         ["polylith.core" "polylith.core.util"]
                         color/none))))

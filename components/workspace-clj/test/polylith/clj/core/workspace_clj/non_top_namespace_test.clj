(ns polylith.clj.core.workspace-clj.non-top-namespace-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.file.interface :as file]
            [polylith.clj.core.workspace-clj.non-top-namespace :as non-top-ns]))

(def paths ["../poly-example/ws02/logo.png"
            "../poly-example/ws02/components/user/src/com/my_ns/user/interface.clj"
            "../poly-example/ws02/components/user/src/com/my_ns/user"
            "../poly-example/ws02/components/user/src/com/my_ns"
            "../poly-example/ws02/components/user/src/com"
            "../poly-example/ws02/components/user/src"
            "../poly-example/ws02/components/user/resources/user/.keep"
            "../poly-example/ws02/components/user/resources/user"
            "../poly-example/ws02/components/user/resources"
            "../poly-example/ws02/components/user/test/com/my_ns/user/interface_test.clj"
            "../poly-example/ws02/components/user/test/com/my_ns/user"
            "../poly-example/ws02/components/user/test/com/my_ns"
            "../poly-example/ws02/components/user/test/com"
            "../poly-example/ws02/components/user/test"
            "../poly-example/ws02/components/user"
            "../poly-example/ws02/components"
            "../poly-example/ws02/readme.md"
            "../poly-example/ws02/bases"
            "../poly-example/ws02/projects"
            "../poly-example/ws02/deps.edn"
            "../poly-example/ws02/development/src"
            "../poly-example/ws02/development"])

(deftest brick->non-top-namespaces--handle-underscore-correctly
  (with-redefs [file/paths-recursively (fn [_] paths)]
    (is (= {}
           (non-top-ns/brick->non-top-namespaces "." "com.my.ns")))))

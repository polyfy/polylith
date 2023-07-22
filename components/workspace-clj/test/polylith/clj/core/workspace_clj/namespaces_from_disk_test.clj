(ns polylith.clj.core.workspace-clj.namespaces-from-disk-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.file.interface :as file]
            [polylith.clj.core.workspace-clj.namespaces-from-disk :as from-disk]))

(def suffixed-top-ns "polylith.clj.core.")
(def interface-ns "interface")

(deftest ns-with-name--nil--returns-false
  (is (= (from-disk/ns-with-name? nil)
         false)))

(deftest ns-with-name--text--returns-false
  (is (= (from-disk/ns-with-name? 'hello)
         false)))

(deftest ns-with-name--only-ns--returns-true
  (is (= (from-disk/ns-with-name? '(ns myns))
         true)))

(deftest ns-with-name--ns-with-name--returns-true
  (is (= (from-disk/ns-with-name? '(ns myns (:require [clojure.string :as str])))
         true)))

(deftest import-list->package-str--handle-dollar-sign-correctly
  (is (= (from-disk/import-list->package-str 'io.opentracing.Tracer$SpanBuilder)
         "io.opentracing")))

(deftest imports--require-is-first-statement--returns-imported-namespaces
  (let [code '(ns polylith.clj.core.file.core
                (:require [clojure.java.io :as io]
                          [polylith.clj.core.util.interface.str :as str-util])
                (:import [java.io File PushbackReader FileNotFoundException]
                         [java.nio.file Files]))]
    (is (= (from-disk/imports code suffixed-top-ns interface-ns)
           ["clojure.java.io"
            "java.io"
            "java.nio.file"
            "polylith.clj.core.util.interface.str"]))))

(deftest imports--require-is-second-statement--returns-imported-namespaces
  (let [code '(ns polylith.spec.interface
                (:gen-class)
                (:require [clojure.test :as test]
                          [polylith.spec.core :as core]))]
    (is (= (from-disk/imports code suffixed-top-ns interface-ns)
           ["clojure.test"
            "polylith.spec.core"]))))

(deftest imports--all-import-forms--returns-imported-packages
  (let [code '(ns polylith.clj.core.file.core
                (:import java.io.File
                         (java.nio.file Files)))]
    (testing "only package names are returned"
      (is (= (from-disk/imports code suffixed-top-ns interface-ns)
             ["java.io" "java.nio.file"])))))

(deftest imports--all-require-forms--return-imported-namespaces
  (let [code '(ns polylith.clj.core.file.core
                (:require lib.a
                          [lib.b]
                          [lib.c :as c]
                          [foo bar [baz :as baz]]))
        result (from-disk/imports code suffixed-top-ns interface-ns)]
    (is (= result
           ["foo.bar" "foo.baz" "lib.a" "lib.b" "lib.c"]))))

(deftest imports--reload--return-imported-namespaces
  (let [code '(ns foo.baz
                (:require [foo.bar] :reload))
        result (from-disk/imports code suffixed-top-ns interface-ns)]
    (is (= result
           ["foo.bar"]))))

(deftest import-when-using-as
  (is (= (from-disk/import '(:require [asalias.comp-a.interface :as comp-a]) "asalias." "interface")
         '("asalias.comp-a.interface"))))

(deftest skip-import-when-using-as-alias-if-interface
  (is (= (from-disk/import '(:require [asalias.comp-a.interface :as-alias comp-a]) "asalias." "interface")
         '())))

(deftest skip-import-when-using-as-alias-if-sub-interface
  (is (= (from-disk/import '(:require [asalias.comp-a.interface.sub :as-alias comp-a]) "asalias." "interface")
         '())))

(deftest import-when-using-as-alias-if-implementation-ns
  (is (= (from-disk/import '(:require [asalias.comp-a.core :as-alias comp-a]) "asalias." "interface")
         ["asalias.comp-a.core"])))

(def file-content '[(System/setProperty "com.sun.xml.bind.v2.bytecode.ClassTailor.noOptimize" "true")
                    (ns polylith.clj.core.tap.core (:require [clojure.string :as str] [portal.api :as portal]))
                    (defn command [cmd] (if (str/blank? cmd) "open" cmd))])

(deftest ->namespace--read-invalid-namespace
  (with-redefs [file/read-file (fn [_] ['--])
                from-disk/namespace-name (fn [_ _] "")]
    (is (= (from-disk/->namespace "." "" "" "" [] "path")
           {:name "", :namespace "", :file-path "path", :imports [], :is-invalid true}))))

(deftest ->namespace--read-namespace
  (with-redefs [file/read-file (fn [_] file-content)
                from-disk/namespace-name (fn [_ _] "core")]
    (is (= (from-disk/->namespace "."
                                  "components/version/src/polylith/clj/core/"
                                  "polylith.clj.core."
                                  "interface"
                                  []
                                  "components/tap/src/polylith/clj/core/tap/core.clj")
           {:name "core"
            :namespace "polylith.clj.core.tap.core"
            :file-path "components/tap/src/polylith/clj/core/tap/core.clj"
            :imports ["clojure.string" "portal.api"]}))))

(deftest ->namespace--read-namespace--ignore-file
  (with-redefs [file/read-file (fn [_] file-content)
                from-disk/namespace-name (fn [_ _] "core")]
    (is (= (from-disk/->namespace "."
                                  "components/version/src/polylith/clj/core/"
                                  "polylith.clj.core."
                                  "interface"
                                  ["core.clj"]
                                  "components/tap/src/polylith/clj/core/tap/core.clj")
           {:name "core"
            :is-ignored true
            :namespace "polylith.clj.core.tap.core"
            :file-path "components/tap/src/polylith/clj/core/tap/core.clj"
            :imports ["clojure.string" "portal.api"]}))))

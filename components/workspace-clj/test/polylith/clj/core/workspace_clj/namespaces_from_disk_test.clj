(ns polylith.clj.core.workspace-clj.namespaces-from-disk-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.file.interface :as file]
            [polylith.clj.core.workspace-clj.namespaces-from-disk :as from-disk]))

(def suffixed-top-ns "polylith.clj.core.")
(def interface-ns "interface")

(deftest ns-with-name--nil--returns-false
  (is (= false
         (from-disk/ns-with-name? nil))))

(deftest ns-with-name--text--returns-false
  (is (= false
         (from-disk/ns-with-name? 'hello))))

(deftest ns-with-name--only-ns--returns-true
  (is (= true
         (from-disk/ns-with-name? '(ns myns)))))

(deftest ns-with-name--ns-with-name--returns-true
  (is (= true
         (from-disk/ns-with-name? '(ns myns (:require [clojure.string :as str]))))))

(deftest import-list->package-str--handle-dollar-sign-correctly
  (is (= "io.opentracing"
         (from-disk/import-list->package-str 'io.opentracing.Tracer$SpanBuilder))))

(deftest imports--require-is-first-statement--returns-imported-namespaces
  (let [code '(ns polylith.clj.core.file.core
                (:require [clojure.java.io :as io]
                          [polylith.clj.core.util.interface.str :as str-util])
                (:import [java.io File PushbackReader FileNotFoundException]
                         [java.nio.file Files]))]
    (is (= ["clojure.java.io"
            "java.io"
            "java.nio.file"
            "polylith.clj.core.util.interface.str"]
           (from-disk/imports code suffixed-top-ns interface-ns)))))

(deftest imports--require-is-second-statement--returns-imported-namespaces
  (let [code '(ns polylith.spec.interface
                (:gen-class)
                (:require [clojure.test :as test]
                          [polylith.spec.core :as core]))]
    (is (= ["clojure.test"
            "polylith.spec.core"]
           (from-disk/imports code suffixed-top-ns interface-ns)))))

(deftest imports--all-import-forms--returns-imported-packages
  (let [code '(ns polylith.clj.core.file.core
                (:import java.io.File
                         (java.nio.file Files)))]
    (testing "only package names are returned"
      (is (= ["java.io" "java.nio.file"]
             (from-disk/imports code suffixed-top-ns interface-ns))))))

(deftest imports--all-require-forms--return-imported-namespaces
  (let [code '(ns polylith.clj.core.file.core
                (:require lib.a
                          [lib.b]
                          [lib.c :as c]
                          [foo bar [baz :as baz]]))
        result (from-disk/imports code suffixed-top-ns interface-ns)]
    (is (= ["foo.bar" "foo.baz" "lib.a" "lib.b" "lib.c"]
           result))))

(deftest imports--reload--return-imported-namespaces
  (let [code '(ns foo.baz
                (:require [foo.bar] :reload))
        result (from-disk/imports code suffixed-top-ns interface-ns)]
    (is (= ["foo.bar"]
           result))))

(deftest import-when-using-as
  (is (= '("asalias.comp-a.interface")
         (from-disk/import '(:require [asalias.comp-a.interface :as comp-a]) "asalias." "interface"))))

(deftest skip-import-when-using-as-alias-if-interface
  (is (= (from-disk/import '(:require [asalias.comp-a.interface :as-alias comp-a]) "asalias." "interface")
         '())))

(deftest skip-import-when-using-as-alias-if-sub-interface
  (is (= '()
         (from-disk/import '(:require [asalias.comp-a.interface.sub :as-alias comp-a]) "asalias." "interface"))))

(deftest import-when-using-as-alias-if-implementation-ns
  (is (= ["asalias.comp-a.core"]
         (from-disk/import '(:require [asalias.comp-a.core :as-alias comp-a]) "asalias." "interface"))))

(def file-content '[(System/setProperty "com.sun.xml.bind.v2.bytecode.ClassTailor.noOptimize" "true")
                    (ns polylith.clj.core.tap.core (:require [clojure.string :as str] [portal.api :as portal]))
                    (defn command [cmd] (if (str/blank? cmd) "open" cmd))])

(deftest ->namespace--read-invalid-namespace
  (with-redefs [file/read-file (fn [_] ['--])
                from-disk/namespace-name (fn [_ _] "")]
    (is (= {:name ""
            :namespace ""
            :file-path "path"
            :imports []
            :is-invalid true}
           (from-disk/->namespace "." "" "" "" "path")))))

(deftest ->namespace--ignore-data-readers
  (with-redefs [file/read-file (fn [_] ['--])
                from-disk/namespace-name (fn [_ _] "")]
    (is (= {:file-path "path/data_readers.clj"
            :imports []
            :name ""
            :namespace ""}
           (from-disk/->namespace "." "" "" "" "path/data_readers.clj")))))

(deftest ->namespace--read-namespace
  (with-redefs [file/read-file (fn [_] file-content)
                from-disk/namespace-name (fn [_ _] "core")]
    (is (= {:name "core"
            :namespace "polylith.clj.core.tap.core"
            :file-path "components/tap/src/polylith/clj/core/tap/core.clj"
            :imports ["clojure.string" "portal.api"]}
           (from-disk/->namespace "."
                                  "components/version/src/polylith/clj/core/"
                                  "polylith.clj.core."
                                  "interface"
                                  "components/tap/src/polylith/clj/core/tap/core.clj")))))

(deftest ->namespace--read-namespace--ignore-file
  (with-redefs [file/read-file (fn [_] file-content)
                from-disk/namespace-name (fn [_ _] "core")]
    (is (= {:name "core"
            :namespace "polylith.clj.core.tap.core"
            :file-path "components/tap/src/polylith/clj/core/tap/core.clj"
            :imports ["clojure.string" "portal.api"]}
           (from-disk/->namespace "."
                                  "components/version/src/polylith/clj/core/"
                                  "polylith.clj.core."
                                  "interface"
                                  "components/tap/src/polylith/clj/core/tap/core.clj")))))

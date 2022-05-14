(ns polylith.clj.core.workspace-clj.readimportsfromdisk-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.workspace-clj.namespaces-from-disk :as from-disk]))

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
           (from-disk/imports code)))))

(deftest imports--require-is-second-statement--returns-imported-namespaces
  (let [code '(ns polylith.spec.interface
                (:gen-class)
                (:require [clojure.test :as test]
                          [polylith.spec.core :as core]))]
    (is (= ["clojure.test"
            "polylith.spec.core"]
           (from-disk/imports code)))))

(deftest imports--all-import-forms--returns-imported-packages
  (let [code '(ns polylith.clj.core.file.core
                (:import java.io.File
                         (java.nio.file Files)))]
    (testing "only package names are returned"
      (is (= ["java.io" "java.nio.file"]
             (from-disk/imports code))))))

(deftest imports--all-require-forms--return-imported-namespaces
  (let [code '(ns polylith.clj.core.file.core
                (:require lib.a
                          [lib.b]
                          [lib.c :as c]
                          [foo bar [baz :as baz]]))
        result (from-disk/imports code)]
    (is (= ["foo.bar" "foo.baz" "lib.a" "lib.b" "lib.c"]
           result))))

(deftest imports--reload--return-imported-namespaces
  (let [code '(ns foo.baz
                (:require [foo.bar] :reload))
        result (from-disk/imports code)]
    (is (= ["foo.bar"]
           result))))

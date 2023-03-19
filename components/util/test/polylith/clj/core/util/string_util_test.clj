(ns polylith.clj.core.util.string-util-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.util.interface.str :as str]))

(deftest skip-until--when-having-a-string-without-a-slash--return-nil
  (is (= (str/skip-until "user.clj" "/")
         nil)))

(deftest skip-until--when-having-a-string-with-a-slash--return-the-string-after-that-slash
  (is (= (str/skip-until "my-ns/user" "/")
         "user")))

(deftest skip-suffix--when-matching-suffix--return-without-the-suffix
  (is (= (str/skip-suffix "user.clj" ".clj")
         "user")))

(deftest skip-suffix--when-no-matching-suffix--return-unchanged
  (is (= (str/skip-suffix "user.cljc" ".clj")
         "user.cljc")))

(deftest skip-suffix--when-string-is-shorter-than-the-suffix--return-unchanged
  (is (= (str/skip-suffix "x" ".clj")
         "x")))

(deftest skip-suffixes--when-having-a-matching-suffix--then-skip-the-suffix
  (is (= (str/skip-suffixes "user.clj" [".cljc" ".clj"])
         "user")))

(deftest skip-suffixes--when-the-suffix-mismatch--return-unchanged
  (is (= (str/skip-suffixes "user.clj" [".cljc" ".cljs"])
         "user.clj")))

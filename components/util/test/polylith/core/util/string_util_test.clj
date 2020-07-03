(ns polylith.core.util.string-util-test
  (:require [clojure.test :refer :all]
            [polylith.core.util.interface.str :as str]))

(deftest skip-until--when-having-a-string-with-a-slash--return-the-string-after-that-slash
  (is (= "user"
         (str/skip-until "my-ns/user" "/"))))

(deftest skip-suffix--when-matching-suffix--return-without-the-suffix
  (is (= "user"
         (str/skip-suffix "user.clj" ".clj"))))

(deftest skip-suffix--when-no-matching-suffix--return-unchanged
  (is (= "user.cljc"
         (str/skip-suffix "user.cljc" ".clj"))))

(deftest skip-suffix--when-string-is-shorter-than-the-suffix--return-unchanged
  (is (= "x"
         (str/skip-suffix "x" ".clj"))))

(deftest skip-suffixes--when-having-a-matching-suffix--then-skip-the-suffix
  (is (= "user"
         (str/skip-suffixes "user.clj" [".cljc" ".clj"]))))

(deftest skip-suffixes--when-the-suffix-mismatch--return-unchanged
  (is (= "user.clj"
         (str/skip-suffixes "user.clj" [".cljc" ".cljs"]))))

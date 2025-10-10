(ns polylith.clj.core.ws-updater.interface-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.ws-updater.interface :as ws-updater]))

(deftest set-value--boolean
  (is (= {:top {:sub {:k true}}}
         (ws-updater/set-value {:top {:sub {:k false}}} ["top" "sub" "k"] nil "true"))))

(deftest set-value--long
  (is (= {:top {:sub {:k 555}}}
         (ws-updater/set-value {:top {:sub {:k 123}}} ["top" "sub" "k"] nil "555"))))

(deftest set-value--string
  (is (= {:top {:k "ccc"}}
         (ws-updater/set-value {:top {:k "aaa"}} ["top" "k"] nil "ccc"))))

(deftest set-value--keyword
  (is (= {:top {:k :ccc}}
         (ws-updater/set-value {:top {:k :iii}} ["top" "k"] nil "ccc"))))

(deftest set-value--with-boolean-type
  (is (= {:top {:k true}}
         (ws-updater/set-value {:top {}} ["top" "k"] "boolean" "true"))))

(deftest set-value--with-keyword-type
  (is (= {:top {:k :ccc}}
         (ws-updater/set-value {:top {}} ["top" "k"] "keyword" "ccc"))))

(deftest set-value--symbol
  (is (= {:top {:k 'ccc}}
         (ws-updater/set-value {:top {:k 'iii}} ["top" "k"] nil "ccc"))))

(deftest set-value--vector-of-strings
  (is (= {:top {:k ["aaa" "bbb"]}}
         (ws-updater/set-value {:top {:k ["a" "b"]}} ["top" "k"] nil ["aaa" "bbb"]))))

(deftest set-value--vector-of-keywords
  (is (= {:top {:k [:aaa :bbb]}}
         (ws-updater/set-value {:top {:k [:a :b]}} ["top" "k"] nil [:aaa :bbb]))))

(deftest set-value--as-string--no-type
  (is (= {:top {:k "123"}}
         (ws-updater/set-value {:top {}} ["top" "k"] nil "123"))))

(deftest set-value--as-string--string
  (is (= {:top {:k "123"}}
         (ws-updater/set-value {:top {}} ["top" "k"] "string" "123"))))

(deftest set-value--as-string--unknown-type
  (is (= {:top {:k "123"}}
         (ws-updater/set-value {:top {}} ["top" "k"] "xxx" "123"))))

(deftest set-value--as-long
  (is (= {:top {:k 123}}
         (ws-updater/set-value {:top {}} ["top" "k"] "long" "123"))))

(deftest set-value--as-keyword
  (is (= {:top {:k :aaa}}
         (ws-updater/set-value {:top {}} ["top" "k"] "keyword" "aaa"))))

(deftest set-value--as-symbol
  (is (= {:top {:k 'aaa}}
         (ws-updater/set-value {:top {}} ["top" "k"] "symbol" "aaa"))))

(deftest set-value--as-booleans
  (is (= {:top {:k [true]}}
         (ws-updater/set-value {:top {}} ["top" "k"] "booleans" "true"))))

(deftest set-value--as-longs
  (is (= {:top {:k [123]}}
         (ws-updater/set-value {:top {}} ["top" "k"] "longs" "123"))))

(deftest set-value--as-strings
  (is (= {:top {:k ["123"]}}
         (ws-updater/set-value {:top {}} ["top" "k"] "strings" "123"))))

(deftest set-value--as-strings--where-existing-vector-exists
  (is (= {:top {:k ["123"]}}
         (ws-updater/set-value {:top {:k []}} ["top" "k"] "strings" "123"))))


(deftest set-value--as-keywords
  (is (= {:top {:k [:aaa]}}
         (ws-updater/set-value {:top {}} ["top" "k"] "keywords" "aaa"))))

(deftest set-value--as-symbols
  (is (= {:top {:k ['aaa]}}
         (ws-updater/set-value {:top {}} ["top" "k"] "symbols" "aaa"))))

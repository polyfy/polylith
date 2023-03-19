(ns polylith.clj.core.util.string-util-split-text
  (:require [clojure.test :refer :all]
            [polylith.clj.core.util.interface.str :as str-util]))

;; todo: gör om så att "hello " returnerar ["hello" ""]

(defn split-text [text]
  (str-util/split-text text))

(deftest split-empty-word
  (is (= (split-text "")
         [""])))

(deftest split-empty-word2
  (is (= (split-text " ")
         [""])))

(deftest split-word
  (is (= (split-text "hello")
         ["hello"])))

(deftest split-word-with-initial-space
  (is (= (split-text " hello")
         ["" "hello"])))

(deftest split-word-ending-with-space
  (is (= (split-text "hello ")
         ["hello" ""])))

(deftest split-several-words
  (is (= (split-text "hello my friend")
         ["hello" "my" "friend"])))

(deftest split-several-words-where-some-are-quoted
  (is (= (split-text "hello, \"you are my best\" friend")
         ["hello," "\"you are my best\"" "friend"])))

(deftest split-several-words-containing-repeating-spaces
  (is (= (split-text "hello  my friend")
         ["hello" "my" "friend"])))

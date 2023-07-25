(ns ^:no-doc polylith.clj.core.util.str
  (:require [clojure.string :as str]
            [polylith.clj.core.util.core :as core])
  (:refer-clojure :exclude [drop-last]))

(defn drop-last [n string]
  (str/join (clojure.core/drop-last n string)))

(defn take-until [string separator]
  (when string
    (let [index (str/index-of string separator)]
      (when index
        (subs string 0 index)))))

(defn skip-until [string separator]
  (when string
    (let [index (str/index-of string separator)]
      (when index
        (subs string (+ (count separator) index))))))

(defn skip-prefix [string prefix]
  (when string
    (if (str/starts-with? string prefix)
      (subs string (count prefix))
      string)))

(defn skip-suffix [string suffix]
  (when string
    (if (str/ends-with? string suffix)
      (let [string-cnt (count string)
            suffix-cnt (count suffix)]
        (if (< suffix-cnt string-cnt)
          (subs string 0 (- string-cnt suffix-cnt))
          string))
      string)))

(defn skip-suffixes [string suffixes]
  (if-let [suffix (core/find-first #(str/ends-with? string %) suffixes)]
    (skip-suffix string suffix)
    string))

(defn spaces [n#spaces]
  (str/join (take n#spaces (repeat " "))))

(defn line [length]
  (str/join (take length (repeat "-"))))

(defn add-first [words val]
  (cons val words))

(defn keep?
  "The split-text function will generate empty strings
   for initial separators (e.g. spaces) and if more than one
   space is repeated within the text, this function
   helps removing these spaces, except for initial ones."
  [[index word] words]
  (or (not= "" word)
      (zero? index)
      (= index (-> words count dec))))

(defn split-text
  "Splits a text, similar to string/split, but with two difference:
   - when a quote is found in the text, it stops splitting spaces
     utill a new quote is found.
   - if the text ends with space (separator) then it will return
     an empty string, in the same way if the text starts with
     space(s)."
  [text separator]
  (if (str/blank? text)
    [""]
    (let [words (vec (loop [quoted? false
                            line text
                            word ""
                            result []]
                       (if (= "" line)
                         (conj result word)
                         (let [c (-> line first str)
                               separator? (= c separator)
                               quote? (= c "\"")
                               new-word (str word c)
                               new-line (subs line 1)]
                           (if quoted?
                             (if quote?
                               (recur false new-line new-word result)
                               (recur true new-line new-word result))
                             (if quote?
                               (recur true new-line new-word result)
                               (if separator?
                                 (recur false new-line "" (conj result word))
                                 (recur false new-line new-word result))))))))]
      (mapv second (filter #(keep? % words) (map-indexed vector words))))))

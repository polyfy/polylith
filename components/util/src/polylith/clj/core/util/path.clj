(ns ^:no-doc polylith.clj.core.util.path
  (:require [clojure.string :as str]
            [polylith.clj.core.util.str :as str-util]))

(defn relative-path [current-path path]
  (let [str1 (str-util/skip-suffix current-path "/")
        str2 (str-util/skip-suffix path "/")
        split1 (str/split str1 #"/")
        split2 (str/split str2 #"/")
        matching-vec (map first
                          (take-while (fn [[s1 s2]] (= s1 s2))
                                      (mapv vector split1 split2)))
        matching-start (str (str/join "/" matching-vec)
                            (if (and (> (count split1) (count matching-vec))
                                     (> (count split2) (count matching-vec)))
                              "/" ""))
        matching-cnt (count matching-start)
        rest1 (subs str1 matching-cnt)
        rest2 (subs str2 matching-cnt)
        levels (+ (count (filter #(= % \/) rest1))
                  (if (or (= "" rest1)
                          (= "" rest2))
                    0 1))]
    (str (str/join (repeat levels "../"))
         rest2)))

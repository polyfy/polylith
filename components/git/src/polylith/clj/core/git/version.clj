(ns polylith.clj.core.git.version
  (:require [clojure.string :as str]))

(defn string-int? [str]
  (try
    (Integer/parseInt str)
    true
    (catch Exception _
      false)))

(defn version? [pattern tag]
  (let [cnt (or (str/index-of pattern "*") (count pattern))]
    (and (> (count tag) cnt)
         (string-int? (subs tag cnt (inc cnt))))))

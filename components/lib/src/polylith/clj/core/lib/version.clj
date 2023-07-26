(ns ^:no-doc polylith.clj.core.lib.version
  (:require [clojure.string :as str]
            [polylith.clj.core.util.interface.str :as str-util]))

(defn clean-suffix [path]
  (-> (str-util/skip-suffix path ".jar")
      (str-util/skip-suffix ".zip")))

(defn version [path]
  (try
    (let [filename (last (str/split path #"/"))]
      (first (re-find (re-matcher #"(\d)(?:\S+)" (clean-suffix filename)))))
    (catch Exception _)))

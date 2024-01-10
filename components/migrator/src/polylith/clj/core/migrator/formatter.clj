(ns ^:no-doc polylith.clj.core.migrator.formatter
  (:require [clojure.walk :as walk]
            [zprint.core :as zp])
  (:import (clojure.lang PersistentArrayMap)
           (java.util HashMap)))

(defn convert-to-hmap [^PersistentArrayMap m]
  (if (map? m)
    (HashMap. m)
    m))

(defn format-content [name key-order content]
  (let [safe-content (walk/postwalk convert-to-hmap content)
        content-str (with-out-str (prn safe-content))]
    (zp/zprint-file-str content-str
                        (str name "/deps.edn")
                        {:width  120
                         :map    {:comma? false
                                  :force-nl? true
                                  :key-order key-order}
                         :vector {:respect-nl? true
                                  :wrap-coll? false}})))

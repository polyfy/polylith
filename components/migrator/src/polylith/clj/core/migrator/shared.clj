(ns polylith.clj.core.migrator.shared
  (:require [zprint.core :as zp]))

(defn hmap [m]
  (java.util.HashMap. m))

(defn format-content [name key-order content]
  (str (zp/zprint-file-str (str content)
                           (str name "/deps.edn")
                           {:width  120
                            :map    {:comma? false
                                     :force-nl? true
                                     :key-order key-order}
                            :vector {:respect-nl? true
                                     :wrap-coll? false}})
       "\n"))

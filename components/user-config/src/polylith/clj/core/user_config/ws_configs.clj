(ns ^:no-doc polylith.clj.core.user-config.ws-configs
  (:require [clojure.string :as str]
            [polylith.clj.core.util.interface.str :as str-util]))

(defn path-name [path]
  (if-let [name (str-util/take-until (str/reverse path) "/")]
    (str/reverse name)
    path))

(defn with-name [{:keys [dir file name]}]
  (let [name (cond
               name name
               dir (path-name dir)
               file (str-util/skip-suffix (path-name file) ".edn")
               :else "MISSING")]
    (cond-> {:name name}
            dir (assoc :dir dir)
            file (assoc :file file))))

(defn ws-shortcuts [user-config]
  (vec (sort-by :name
                (map with-name
                     (:ws-shortcuts user-config)))))

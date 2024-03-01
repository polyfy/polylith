(ns ^:no-doc polylith.clj.core.user-config.ws-configs
  (:require [clojure.string :as str]
            [polylith.clj.core.user-config.core :as core]
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

(defn ws-shortcuts []
  (let [{:keys [ws-shortcuts]} (core/config-content)]
    (vec (sort-by :name
                  (map with-name
                       ws-shortcuts)))))

(defn with-shortcut-root-dir [path]
  (when path
    (if-let [root-dir (:ws-shortcuts-root-dir (core/config-content))]
      (str (str-util/skip-if-ends-with root-dir "/") "/" path)
      path)))

(comment
  (ws-shortcuts)
  (with-shortcut-root-dir "dir")
  #__)

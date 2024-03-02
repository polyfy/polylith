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

(defn ws-shortcuts-paths []
  (let [config (core/config-content)]
    (vec (sort-by :name
                  (map with-name
                       (-> config :ws-shortcuts :paths))))))

(defn with-shortcut-root-dir [path]
  (let [config (core/config-content)]
    (when path
      (if-let [root-dir (-> config :ws-shortcuts :root-dir)]
        (str (str-util/skip-if-ends-with root-dir "/") "/" path)
        path))))

(comment
  (core/config-content)
  (ws-shortcuts-paths)
  (with-shortcut-root-dir "dir")
  #__)

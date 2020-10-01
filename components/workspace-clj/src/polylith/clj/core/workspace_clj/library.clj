(ns polylith.clj.core.workspace-clj.library
  (:require [clojure.string :as str]
            [polylith.clj.core.file.interface :as file]
            [polylith.clj.core.user-config.interface :as user-config]
            [polylith.clj.core.util.interface :as util]))

(defn lib-size-kb [name version]
  (try
    (let [lib-info (str/split (str name) #"/")
          lib-ns (first lib-info)
          lib-name (or (second lib-info) lib-ns)
          path (str (user-config/m2-dir)
                    "/repository/"
                    (str/replace lib-ns "." "/")
                    "/" lib-name
                    "/" version
                    "/" lib-name "-" version ".jar")]
      (file/size path))
    (catch Exception _)))

(defn with-size [[name {:keys [mvn/version] :as value}]]
  (if version
    (if-let [size (lib-size-kb name version)]
      [name (assoc value :size size)]
      [name value])
    [name value]))

(defn with-sizes [library-map]
  (util/stringify-and-sort-map (into {} (map with-size library-map))))

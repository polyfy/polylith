(ns polylith.clj.core.lib.mvn-size
  (:require [clojure.string :as str]
            [clojure.walk :as walk]
            [polylith.clj.core.file.interface :as file]
            [polylith.clj.core.user-config.interface :as user-config]))

(defn lib-size-bytes [name version]
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

(defn with-maven [value]
  (assoc (walk/postwalk-replace {:mvn/version :version} value) :type "maven"))

(defn with-size [lib-name version value]
  (if-let [size (lib-size-bytes lib-name version)]
    [lib-name (assoc (with-maven value) :size size)]
    [lib-name (with-maven value)]))

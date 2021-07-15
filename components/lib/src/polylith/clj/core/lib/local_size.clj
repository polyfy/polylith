(ns polylith.clj.core.lib.local-size
  (:require [clojure.string :as str]
            [polylith.clj.core.lib.version :as version]
            [polylith.clj.core.file.interface :as file]))

(defn file-size [path]
  (when (file/exists path)
    (file/size path)))

(defn relative-path [path entity-root-path]
  (if entity-root-path
    (if (str/starts-with? path "../../")
      (subs path 6)
      (str entity-root-path "/" path))
    path))

(defn with-size-and-version [lib-name path value entity-root-path]
  (if-let [size (file-size path)]
    [lib-name (assoc value :type "local"
                           :path (relative-path path entity-root-path)
                           :size size
                           :version (version/version path))]
    [lib-name (assoc value :type "local"
                           :path (relative-path path entity-root-path))]))

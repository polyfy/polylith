(ns polylith.clj.core.lib.local-size
  (:require [polylith.clj.core.lib.version :as version]
            [polylith.clj.core.file.interface :as file]))

(defn file-size [path]
  (when (file/exists path)
    (file/size path)))

(defn with-size-and-version [lib-name path value]
  (if-let [size (file-size path)]
    [lib-name (assoc value :type "local" :path path :size size :version (version/version path))]
    [lib-name (assoc value :type "local" :path path)]))

(ns polylith.clj.core.lib.local-size
  (:require [polylith.clj.core.lib.version :as version]
            [polylith.clj.core.file.interface :as file]
            [polylith.clj.core.common.interface :as common]))

(defn file-size [ws-dir path entity-root-path]
  (let [absolute-path (str ws-dir "/" (common/absolute-path path entity-root-path))]
    (if (file/exists absolute-path)
      (file/size absolute-path)
      0)))

(defn with-size-and-version [ws-dir lib-name path value entity-root-path]
  (let [size (file-size ws-dir path entity-root-path)
        version (version/version path)
        absolute-path (common/absolute-path path entity-root-path)]
    [lib-name (cond-> (assoc value :type "local")
                      absolute-path (assoc :path absolute-path)
                      size (assoc :size size)
                      version (assoc :version version))]))

(ns polylith.clj.core.lib.local-size
  (:require [polylith.clj.core.lib.version :as version]
            [polylith.clj.core.file.interface :as file]
            [polylith.clj.core.common.interface :as common]))

(defn file-size [ws-dir path entity-root-path]
  (let [absolute-path (str ws-dir "/" (common/absolute-path path entity-root-path))]
    (when (file/exists absolute-path)
      (file/size absolute-path))))

(defn with-size-and-version [ws-dir lib-name path value entity-root-path]
  (if-let [size (file-size ws-dir path entity-root-path)]
     [lib-name (assoc value :type "local"
                            :path (common/absolute-path path entity-root-path)
                            :size size
                            :version (version/version path))]
     [lib-name (assoc value :type "local"
                            :path (common/absolute-path path entity-root-path)
                            :size 0
                            :version "-")]))

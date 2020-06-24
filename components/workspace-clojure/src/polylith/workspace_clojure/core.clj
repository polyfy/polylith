(ns polylith.workspace-clojure.core
  (:require [clojure.string :as str]
            [polylith.common.interface :as common]
            [polylith.file.interface :as file]
            [polylith.util.interface :as util]
            [polylith.workspace-clojure.environment :as env]
            [polylith.workspace-clojure.components-from-disk :as components-from-disk]
            [polylith.workspace-clojure.bases-from-disk :as bases-from-disk]))

(defn workspace-from-disk
  ([ws-path]
   (let [config (read-string (slurp (str ws-path "/deps.edn")))]
     (workspace-from-disk ws-path config)))
  ([ws-path {:keys [polylith]}]
   (let [{:keys [top-namespace]} polylith
         top-ns (common/top-namespace top-namespace)
         top-src-dir (str/replace top-ns "." "/")
         component-names (file/directory-paths (str ws-path "/components"))
         components (components-from-disk/read-components ws-path top-src-dir component-names)
         bases (bases-from-disk/read-bases ws-path top-src-dir)
         environments (env/environments ws-path)
         settings (util/ordered-map :top-namespace top-namespace)]
     (util/ordered-map :ws-path ws-path
                       :settings settings
                       :components components
                       :bases bases
                       :environments environments))))

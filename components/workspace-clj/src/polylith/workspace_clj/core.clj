(ns polylith.workspace-clj.core
  (:require [clojure.string :as str]
            [polylith.file.interface :as file]
            [polylith.shared.interface :as shared]
            [polylith.util.interface :as util]
            [polylith.workspace-clj.environment :as env]
            [polylith.workspace-clj.components-from-disk :as components-from-disk]
            [polylith.workspace-clj.bases-from-disk :as bases-from-disk]))

(defn workspace-from-disk
  ([ws-path]
   (let [config (read-string (slurp (str ws-path "/deps.edn")))]
     (workspace-from-disk ws-path config)))
  ([ws-path {:keys [polylith mvn/repos deps paths] :as config}]
   (let [{:keys [top-namespace
                 compile-path
                 thread-pool-size]} polylith
         top-ns (shared/top-namespace top-namespace)
         top-src-dir (str/replace top-ns "." "/")
         component-names (file/directory-paths (str ws-path "/components"))
         components (components-from-disk/read-components ws-path top-src-dir component-names)
         bases (bases-from-disk/read-bases ws-path top-src-dir)
         environments (env/environments config)
         settings (util/ordered-map :top-namespace top-namespace
                                    :compile-path compile-path
                                    :thread-pool-size thread-pool-size
                                    :maven-repos repos)]

     (util/ordered-map :ws-path ws-path
                       :settings settings
                       :deps deps
                       :paths paths
                       :components components
                       :bases bases
                       :environments environments))))

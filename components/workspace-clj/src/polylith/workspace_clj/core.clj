(ns polylith.workspace-clj.core
  (:require [clojure.string :as str]
            [polylith.common.interface :as common]
            [polylith.file.interface :as file]
            [polylith.util.interface :as util]
            [polylith.workspace-clj.environment :as env]
            [polylith.workspace-clj.components-from-disk :as components-from-disk]
            [polylith.workspace-clj.bases-from-disk :as bases-from-disk]))

(defn workspace-from-disk
  ([ws-path]
   (let [config (read-string (slurp (str ws-path "/deps.edn")))]
     (workspace-from-disk ws-path config)))
  ([ws-path {:keys [polylith mvn/repos] :as config}]
   (let [{:keys [env-prefix
                 top-namespace
                 compile-path
                 thread-pool-size]} polylith
         top-ns (common/top-namespace top-namespace)
         top-src-dir (str/replace top-ns "." "/")
         component-names (file/directory-paths (str ws-path "/components"))
         components (components-from-disk/read-components ws-path top-src-dir component-names)
         bases (bases-from-disk/read-bases ws-path top-src-dir)
         prefix (or env-prefix "env")
         environments (env/environments prefix config)
         settings (util/ordered-map :top-namespace top-namespace
                                    :compile-path compile-path
                                    :thread-pool-size thread-pool-size
                                    :maven-repos repos)]
     (util/ordered-map :ws-path ws-path
                       :settings settings
                       :components components
                       :bases bases
                       :environments environments))))

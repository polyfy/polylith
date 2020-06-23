(ns polylith.workspace-clj.core
  (:require [clojure.string :as str]
            [polylith.common.interface :as common]
            [polylith.file.interface :as file]
            [polylith.util.interface :as util]
            [polylith.util.interface.str :as str-util]
            [polylith.workspace-clj.environment :as env]
            [polylith.workspace-clj.components-from-disk :as components-from-disk]
            [polylith.workspace-clj.bases-from-disk :as bases-from-disk]))

(defn workspace-from-disk
  ([ws-path]
   (let [config (read-string (slurp (str ws-path "/deps.edn")))]
     (workspace-from-disk ws-path config)))
  ([ws-path {:keys [polylith mvn/repos] :as config}]
   (let [{:keys [env-prefix
                 top-namespace]
          :or {env-prefix "env"}} polylith
         top-ns (common/top-namespace top-namespace)
         top-src-dir (str/replace top-ns "." "/")
         component-names (file/directory-paths (str ws-path "/components"))
         components (components-from-disk/read-components ws-path top-src-dir component-names)
         bases (bases-from-disk/read-bases ws-path top-src-dir)
         environments (env/environments env-prefix config)
         prefix (str-util/skip-suffix env-prefix "/")
         settings (util/ordered-map :top-namespace top-namespace
                                    :env-prefix prefix
                                    :maven-repos repos)]
     (util/ordered-map :ws-path ws-path
                       :settings settings
                       :components components
                       :bases bases
                       :environments environments))))

(ns polylith.workspace-clj.core
  (:require [clojure.string :as str]
            [polylith.file.interface :as file]
            [polylith.shared.interface :as shared]
            [polylith.environment.interface :as env]
            [polylith.workspace-clj.components-from-disk :as components-from-disk]
            [polylith.workspace-clj.bases-from-disk :as bases-from-disk]))

(defn workspace-from-disk
  ([ws-path]
   (let [config (read-string (slurp (str ws-path "/deps.edn")))]
     (workspace-from-disk ws-path config)))
  ([ws-path {:keys [polylith] :as config}]
   (let [top-ns (shared/top-namespace (:top-namespace polylith))
         top-src-dir (str/replace top-ns "." "/")
         component-names (file/directory-paths (str ws-path "/components"))
         components (components-from-disk/read-components-from-disk ws-path top-src-dir component-names)
         bases (bases-from-disk/read-bases-from-disk ws-path top-src-dir)
         environments (env/environments-from-deps-edn config)]
     {:polylith polylith
      :components components
      :bases bases
      :environments environments})))

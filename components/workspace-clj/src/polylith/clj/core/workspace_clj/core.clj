(ns polylith.clj.core.workspace-clj.core
  (:require [clojure.string :as str]
            [polylith.clj.core.common.interfc :as common]
            [polylith.clj.core.file.interfc :as file]
            [polylith.clj.core.util.interfc :as util]
            [polylith.clj.core.user-config.interfc :as user-config]
            [polylith.clj.core.workspace-clj.environment-from-disk :as envs-from-disk]
            [polylith.clj.core.workspace-clj.components-from-disk :as components-from-disk]
            [polylith.clj.core.workspace-clj.bases-from-disk :as bases-from-disk]))

(def ws-reader
  {:name "polylith-clj"
   :project-url "https://github.com/tengstrand/polylith/tree/core"
   :reader-version "1.0"
   :ws-contract-version 1
   :language "Clojure"
   :type-position "postfix"
   :slash "/"
   :file-extensions [".clj" "cljc"]})

(defn workspace-from-disk
  ([ws-path]
   (let [config (read-string (slurp (str ws-path "/deps.edn")))]
     (workspace-from-disk ws-path config)))
  ([ws-path {:keys [polylith]}]
   (let [{:keys [vcs top-namespace interface-ns env->alias]} polylith
         top-ns (common/sufix-ns-with-dot top-namespace)
         top-src-dir (str/replace top-ns "." "/")
         color-mode (user-config/color-mode)
         component-names (file/directory-paths (str ws-path "/components"))
         components (components-from-disk/read-components ws-path top-src-dir component-names interface-ns)
         bases (bases-from-disk/read-bases ws-path top-src-dir)
         environments (envs-from-disk/read-environments ws-path)
         settings (util/ordered-map :vcs (or vcs "git")
                                    :top-namespace top-namespace
                                    :interface-ns (or interface-ns "interface")
                                    :color-mode color-mode
                                    :env->alias env->alias)]
     (util/ordered-map :ws-path ws-path
                       :ws-reader ws-reader
                       :settings settings
                       :components components
                       :bases bases
                       :environments environments))))

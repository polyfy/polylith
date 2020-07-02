(ns polylith.workspace-kotlin.core
  (:require [polylith.workspace-kotlin.settings-config-from-disk :as settings]
            [polylith.workspace-kotlin.build-config-from-disk :as config]
            [polylith.workspace-kotlin.components-from-disk :as cfrom-disk]
            [polylith.common.interface :as common]
            [clojure.string :as str]
            [polylith.file.interface :as file]))

(defn root-dir [dir]
  (if (str/blank? dir)
    dir
    (str "/" dir)))

(defn workspace-from-disk [ws-path]
  (let [{:keys [main-sources
                test-sources]} (config/sources ws-path)
        property->value (settings/settings-from-disk ws-path)
        top-ns (-> property->value :top-namespace common/top-namespace)
        src-root-dir (root-dir (property->value :src-root-dir ""))
        top-src-dir (str/replace top-ns "." "/")
        color-mode (property->value :color-mode "plain")
        src-path (str ws-path src-root-dir)
        component-names (vec (sort (file/directory-paths (str src-path "/components"))))
        components (cfrom-disk/read-components src-path top-src-dir component-names)]
    components))


;(def ws-path "../polylith-kotlin")
;(workspace-from-disk ws-path)
;
;
;(def top-ns "polylith.kotlin.")
;(def src-root-dir (root-dir "kotlin"))
;(def top-src-dir (str/replace top-ns "." "/"))
;(def src-path (str ws-path src-root-dir))

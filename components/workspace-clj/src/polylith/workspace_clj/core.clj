(ns polylith.workspace-clj.core
  (:require [clojure.string :as str]
            [polylith.file.interface :as file]
            [polylith.shared.interface :as shared]
            [polylith.workspace-clj.aliases :as alias]
            [polylith.workspace-clj.componentsfromdisk :as componentsfromdisk]
            [polylith.workspace-clj.basesfromdisk :as basesfromdisk]))

(defn read-workspace-from-disk
  ([ws-path]
   (let [config (read-string (slurp (str ws-path "/deps.edn")))]
     (read-workspace-from-disk ws-path config)))
  ([ws-path {:keys [polylith] :as config}]
   (let [top-ns (shared/top-namespace (:top-namespace polylith))
         top-src-dir (str/replace top-ns "." "/")
         component-names (file/directory-paths (str ws-path "/components"))
         components (componentsfromdisk/read-components-from-disk ws-path top-src-dir component-names)
         bases (basesfromdisk/read-bases-from-disk ws-path top-src-dir)
         aliases (alias/aliases config)]
         ;interface-names (vec (sort (map #(-> % :interface :name) components)))]
     ;messages (validate/error-messages interface-names components bases)]
     {:polylith polylith
      :components components
      :bases bases
      :aliases aliases})))
;:messages messages}))


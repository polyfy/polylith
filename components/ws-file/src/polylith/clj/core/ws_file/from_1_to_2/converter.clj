(ns ^:no-doc polylith.clj.core.ws-file.from-1-to-2.converter
  (:require [polylith.clj.core.ws-file.from-1-to-2.skip-ws-dir-in-paths :as skip-ws-dir-in-paths]
            [polylith.clj.core.ws-file.from-1-to-2.update-project-deps :as update-project-deps]
            [polylith.clj.core.ws-file.from-1-to-2.rename-to-arglist :as rename-to-arglist]))

(defn convert [workspace]
  (-> workspace
      (skip-ws-dir-in-paths/convert)
      (update-project-deps/convert)
      (rename-to-arglist/convert)))

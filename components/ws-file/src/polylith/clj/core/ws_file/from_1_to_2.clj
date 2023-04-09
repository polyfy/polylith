(ns polylith.clj.core.ws-file.from-1-to-2
  (:require [polylith.clj.core.ws-file.from-1-to-2.update-project-deps :as update-project-deps]))

(defn convert [workspace]
  (-> workspace
      (shorten-paths/skip-ws-dir-in-paths)
      (update-project-deps/convert)))

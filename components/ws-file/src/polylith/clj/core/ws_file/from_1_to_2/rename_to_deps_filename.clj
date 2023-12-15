(ns ^:no-doc polylith.clj.core.ws-file.from-1-to-2.rename-to-deps-filename)

(defn rename-config-filename [project]
  (clojure.set/rename-keys project {:config-filename :deps-filename}))

(defn convert [{:keys [projects] :as workspace}]
  (assoc workspace :projects (mapv rename-config-filename projects)))

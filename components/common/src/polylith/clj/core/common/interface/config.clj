(ns ^:no-doc polylith.clj.core.common.interface.config)

(defn src-paths [config]
  (-> config :paths))

(defn test-paths [config]
  (-> config :aliases :test :extra-paths))

(defn source-paths [config]
  (concat (src-paths config)
          (test-paths config)))

(defn keep-lib-versions [keep-lib-versions-from-config {:keys [keep-lib-versions]}]
  (let [lib-versions (or keep-lib-versions-from-config keep-lib-versions)]
    (when (sequential? lib-versions)
      (mapv str lib-versions))))

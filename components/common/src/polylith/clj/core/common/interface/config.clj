(ns ^:no-doc polylith.clj.core.common.interface.config)

(defn src-paths [config]
  (-> config :paths))

(defn test-paths [config]
  (-> config :aliases :test :extra-paths))

(defn source-paths [config]
  (concat (src-paths config)
          (test-paths config)))

(defn keep-lib-versions [{:keys [keep-lib-versions]}]
  (when (sequential? keep-lib-versions)
    (mapv str keep-lib-versions)))

(ns ^:no-doc polylith.clj.core.common.interface.config)

(defn src-paths [config]
  (-> config :paths))

(defn test-paths [config]
  (-> config :aliases :test :extra-paths))

(defn source-paths [config]
  (concat (src-paths config)
          (test-paths config)))

(defn settings-value [key entity-config settings-config]
  (let [entity-value (-> entity-config :config key)
        settings-value (key settings-config)
        value (or entity-value settings-value)]
    (when (sequential? value)
      (mapv str value))))

(ns polylith.clj.core.common.interface.config
  (:require [polylith.clj.core.common.config.read :as read]
            [polylith.clj.core.common.config.validate :as validate]))

(defn src-paths [config]
  (-> config :paths))

(defn test-paths [config]
  (-> config :aliases :test :extra-paths))

(defn source-paths [config]
  (concat (src-paths config)
          (test-paths config)))

(defn read-deps-file [deps-path]
  (read/read-deps-file deps-path))

(defn valid-ws-root-config-file-found? [path color-mode]
  (validate/valid-ws-root-config-file-found? path color-mode))

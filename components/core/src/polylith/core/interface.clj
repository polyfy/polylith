(ns polylith.core.interface
  (:require [polylith.core.aliases :as aliases]
            [polylith.core.circulardeps :as circulardeps]
            [polylith.core.validate :as validate]
            [polylith.core.dependencies :as deps]))

(defn aliases [{:keys [paths deps aliases]}]
  (aliases/aliases paths deps aliases))

(defn circular-deps [interfaces components]
  (circulardeps/circular-deps interfaces components))

(defn dependencies [top-ns brick components brick-imports]
  (deps/dependencies top-ns brick components brick-imports))

(defn error-messages [interface-names components bases]
  (validate/error-messages interface-names components bases))

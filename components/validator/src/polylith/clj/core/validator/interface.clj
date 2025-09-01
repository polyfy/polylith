(ns ^:no-doc polylith.clj.core.validator.interface
  (:require [polylith.clj.core.validator.core :as core]
            [polylith.clj.core.validator.datashape.dispatcher :as dispatch]
            [polylith.clj.core.validator.datashape.toolsdeps2 :as toolsdeps2]
            [polylith.clj.core.validator.message-printer :as message-printer]))

(defn has-errors? [messages]
  (core/has-errors? messages))

(defn warning-messages [messages]
  (core/warning-messages messages))

(defn error-messages [messages]
  (core/error-messages messages))

(defn print-messages [workspace]
  (message-printer/print-messages workspace))

(defn validate-brick-package-config [config filename]
  (toolsdeps2/validate-brick-package-config config filename))

(defn validate-deployable-project-package-config [config filename]
  (toolsdeps2/validate-deployable-project-package-config config filename))

(defn validate-dev-project-package-config [config filename]
  (toolsdeps2/validate-development-project-package-config config filename))

(defn validate-project-dev-config [ws-type config filename]
  (dispatch/validate-project-dev-deps-config ws-type config filename))

(defn validate-brick-deps-config [_ config filename]
  (toolsdeps2/validate-brick-deps-config config filename))

(defn validate-workspace-config [config]
  (toolsdeps2/validate-workspace-config config))

(defn validate-deployable-project-deps-config [ws-type config filename]
  (dispatch/validate-deployable-project-deps-config ws-type config filename))

(defn validate-ws [settings configs paths interface-names interfaces profiles components bases projects config-errors interface-ns user-input color-mode]
  (core/validate-ws settings configs paths interface-names interfaces profiles components bases projects config-errors interface-ns user-input color-mode))

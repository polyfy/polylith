(ns polylith.clj.core.validator.m206-unreadable-namespace
  (:require [polylith.clj.core.util.interface :as util]
            [polylith.clj.core.util.interface.color :as color]))

(defn unreadable-ns [{:keys [file-path invalid]} type name color-mode]
  (when invalid
    (let [message (str "Unreadable namespaces in " (color/brick type name color-mode) ": \"" file-path)]
      [(util/ordered-map :type "warning"
                         :code 206
                         :message (color/clean-colors message)
                         :colorized-message message)])))

(defn unreadable-nss [{:keys [type name namespaces]} color-mode]
  (concat
    (mapcat #(unreadable-ns % type name color-mode) (:src namespaces))
    (mapcat #(unreadable-ns % type name color-mode) (:test namespaces))))

(defn warnings [components bases projects color-mode]
  (mapcat #(unreadable-nss % color-mode)
          (concat components bases projects)))

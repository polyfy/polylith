(ns ^:no-doc polylith.clj.core.validator.m111-unreadable-namespace
  (:require [polylith.clj.core.util.interface :as util]
            [polylith.clj.core.util.interface.color :as color]))

(defn unreadable-ns [{:keys [file-path is-invalid empty-file error-message]} type name color-mode]
  (when is-invalid
    (let [message-prefix (str "Unreadable namespace in " (color/brick type name color-mode) ": " file-path)
          message (cond
                    empty-file
                    (str message-prefix ". File is empty. Please add a namespace declaration.")

                    error-message
                    (str message-prefix ". " error-message)

                    :else
                    (str message-prefix ". To solve this problem, execute 'poly help check' and follow the instructions for error 111."))]
      [(util/ordered-map :type "error"
                         :code 111
                         :message (color/clean-colors message)
                         :colorized-message message)])))

(defn unreadable-nss [{:keys [type name namespaces]} color-mode]
  (concat
    (mapcat #(unreadable-ns % type name color-mode) (:src namespaces))
    (mapcat #(unreadable-ns % type name color-mode) (:test namespaces))))

(defn errors [components bases projects color-mode]
  (mapcat #(unreadable-nss % color-mode)
          (concat components bases projects)))

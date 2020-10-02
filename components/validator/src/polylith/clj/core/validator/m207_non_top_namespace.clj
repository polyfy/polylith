(ns polylith.clj.core.validator.m207-non-top-namespace
  (:require [polylith.clj.core.util.interface :as util]
            [polylith.clj.core.util.interface.color :as color]))

(defn warning [{:keys [name type non-top-ns]} color-mode]
  (let [message (str "Non top namespace " (color/namespc non-top-ns color-mode) " was found in " (color/brick type name color-mode) ".")]
    (util/ordered-map :type "warning"
                      :code 207
                      :message (color/clean-colors message)
                      :colorized-message message)))

(defn warnings [non-top-namespaces color-mode]
  (map #(warning % color-mode)
       (sort-by (juxt :type :name)
                (set (map #(select-keys % [:type :name :non-top-ns])
                          non-top-namespaces)))))

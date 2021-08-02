(ns polylith.clj.core.validator.m205-non-top-namespace
  (:require [polylith.clj.core.util.interface :as util]
            [polylith.clj.core.util.interface.color :as color]))

(defn warning [{:keys [name type non-top-ns]} color-mode]
  (let [message (str "Non top namespace " (color/namespc non-top-ns color-mode) " was found in " (color/brick type name color-mode) ".")]
    (util/ordered-map :type "warning"
                      :code 205
                      :message (color/clean-colors message)
                      :colorized-message message)))

(defn non-top-nss [{:keys [name type non-top-namespaces]}]
  (mapv #(assoc (select-keys % [:non-top-ns]) :name name :type type)
        non-top-namespaces))

(defn warnings [components bases color-mode]
  (map #(warning % color-mode)
       (sort-by (juxt :type :name)
                (set (mapcat non-top-nss
                             (concat components bases))))))

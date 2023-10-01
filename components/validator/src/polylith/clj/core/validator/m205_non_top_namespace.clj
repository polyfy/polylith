(ns ^:no-doc polylith.clj.core.validator.m205-non-top-namespace
  (:require [clojure.string :as str]
            [polylith.clj.core.util.interface :as util]
            [polylith.clj.core.util.interface.color :as color]))

(defn warning [[[name type non-top-ns] file-maps] color-mode]
  (let [files (str/join ", " (mapv :file file-maps))
        message (str "Non top namespace " (color/namespc non-top-ns color-mode) " was found in " (color/brick type name color-mode) ", files: " (color/grey color-mode files))]
    (util/ordered-map :type "warning"
                      :code 205
                      :message (color/clean-colors message)
                      :colorized-message message)))

(defn non-top-nss [{:keys [name type non-top-namespaces]}]
  (mapv #(assoc (select-keys % [:non-top-ns :file]) :name name :type type)
        non-top-namespaces))

(defn warnings [components bases color-mode]
  (map #(warning % color-mode)
       (group-by (juxt :name :type :non-top-ns) (mapcat non-top-nss
                                                        (concat components bases)))))

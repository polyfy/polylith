(ns polylith.clj.core.validator.m207-reference-to-missing-namespace-in-ns-lib
  (:require [clojure.set :as set]
            [clojure.string :as str]
            [polylith.clj.core.lib-dep.interfc :as lib-dep]
            [polylith.clj.core.util.interfc :as util]
            [polylith.clj.core.util.interfc.color :as color]))

(defn included-namespaces [settings brick]
  (:included-namespaces (lib-dep/dependencies settings brick)))

(defn warnings [settings components bases color-mode]
  (let [bricks (concat components bases)
        ns->lib (:ns->lib settings)
        mapped-namespaces (set (map first ns->lib))
        used-namespaces (set (mapcat #(included-namespaces settings %) bricks))
        missing-namespaces (set/difference mapped-namespaces used-namespaces)
        message (str "Reference to missing namespace was found in the :ns->lib mapping: "
                     (color/library (str/join ", " missing-namespaces) color-mode))]
    (when (-> missing-namespaces empty? not)
      [(util/ordered-map :type "warning"
                         :code 207
                         :message (color/clean-colors message)
                         :colorized-message message)])))

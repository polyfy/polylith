(ns polylith.clj.core.validator.m206-reference-to-missing-namespace-in-ns-lib
  (:require [clojure.set :as set]
            [clojure.string :as str]
            [polylith.clj.core.lib-dep.interface :as lib-dep]
            [polylith.clj.core.util.interface :as util]
            [polylith.clj.core.util.interface.color :as color]))

(defn included-namespaces [settings brick]
  (:included-namespaces (lib-dep/dependencies settings brick)))

(defn warnings [settings components bases color-mode]
  (let [bricks (concat components bases)
        ns-to-lib (:ns-to-lib settings)
        mapped-namespaces (set (map first ns-to-lib))
        used-namespaces (set (mapcat #(included-namespaces settings %) bricks))
        missing-namespaces (set/difference mapped-namespaces used-namespaces)
        message (str "Reference to missing namespace was found in the :ns-to-lib mapping: "
                     (color/library (str/join ", " missing-namespaces) color-mode))]
    (when (-> missing-namespaces empty? not)
      [(util/ordered-map :type "warning"
                         :code 206
                         :message (color/clean-colors message)
                         :colorized-message message)])))

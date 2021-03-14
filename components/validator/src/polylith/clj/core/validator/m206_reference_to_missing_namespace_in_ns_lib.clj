(ns polylith.clj.core.validator.m206-reference-to-missing-namespace-in-ns-lib
  (:require [clojure.set :as set]
            [clojure.string :as str]
            [polylith.clj.core.lib.interface :as lib]
            [polylith.clj.core.util.interface :as util]
            [polylith.clj.core.util.interface.color :as color]))

(defn included-namespaces [{:keys [top-namespace ns-to-lib]} {:keys [namespaces-src]}]
  (lib/included-namespaces top-namespace ns-to-lib namespaces-src))

(defn warnings [{:keys [ws-type ns-to-lib] :as settings} components bases color-mode]
  (let [bricks (concat components bases)
        mapped-namespaces (set (map first ns-to-lib))
        used-namespaces (set (mapcat #(included-namespaces settings %) bricks))
        missing-namespaces (set/difference mapped-namespaces used-namespaces)
        message (str "Reference to missing namespace was found in the :ns-to-lib mapping: "
                     (color/library (str/join ", " missing-namespaces) color-mode))]
    (when (and (= :toolsdeps1 ws-type)
               (-> missing-namespaces empty? not))
      [(util/ordered-map :type "warning"
                         :code 206
                         :message (color/clean-colors message)
                         :colorized-message message)])))

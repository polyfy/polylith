(ns polylith.clj.core.validator.m101-illegal-namespace-deps
  (:require [polylith.clj.core.deps.interface :as deps]
            [polylith.clj.core.util.interface :as util]
            [polylith.clj.core.util.interface.color :as color]))

(defn error-message [{:keys [namespace depends-on-interface depends-on-ns]} brick-name type interface-ns color-mode]
  (when namespace
    (let [message (str "Illegal dependency on namespace " (color/namespc depends-on-interface depends-on-ns color-mode)
                       " in " (color/brick type brick-name color-mode) "." (color/namespc namespace color-mode)
                       ". Use " (color/namespc depends-on-interface interface-ns color-mode) " instead to fix the problem.")]
      [(util/ordered-map :type "error"
                         :code 101
                         :message (color/clean-colors message)
                         :colorized-message message
                         :bricks [brick-name])])))

(defn brick-errors [suffixed-top-ns {:keys [name interface type namespaces-src]} interface-names interface-ns color-mode]
  "Checks for dependencies to component interface namespaces other than 'interface'."
  (let [interface-name (:name interface)
        dependencies (deps/interface-ns-deps suffixed-top-ns interface-name interface-names namespaces-src)]
    (mapcat #(error-message % name type interface-ns color-mode)
            (filterv #(not= interface-ns (:depends-on-ns %)) dependencies))))

(defn errors [suffixed-top-ns interface-names components bases interface-ns color-mode]
  (vec (mapcat #(brick-errors suffixed-top-ns % interface-names interface-ns color-mode)
               (concat components bases))))

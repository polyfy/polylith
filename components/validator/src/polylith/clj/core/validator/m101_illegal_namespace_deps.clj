(ns polylith.clj.core.validator.m101-illegal-namespace-deps
  (:require [clojure.string :as str]
            [polylith.clj.core.deps.interface :as deps]
            [polylith.clj.core.util.interface :as util]
            [polylith.clj.core.util.interface.color :as color]))

(defn error-message [{:keys [namespace depends-on-interface depends-on-ns]} brick-name type interface-ns-name color-mode]
  (when namespace
    (let [message (str "Illegal dependency on namespace " (color/namespc depends-on-interface depends-on-ns color-mode)
                       " in " (color/brick type brick-name color-mode) "." (color/namespc namespace color-mode)
                       ". Use " (color/namespc depends-on-interface interface-ns-name color-mode) " instead to fix the problem.")]
      [(util/ordered-map :type "error"
                         :code 101
                         :message (color/clean-colors message)
                         :colorized-message message
                         :bricks [brick-name])])))

(defn get-namespace [{:keys [depends-on-ns]}]
  (if-let [idx (str/index-of depends-on-ns ".")]
    (subs depends-on-ns 0 idx)
    depends-on-ns))

(defn brick-errors
  "Checks for dependencies to component interface namespaces other than 'interface'
   for the 'src' context."
  [suffixed-top-ns {:keys [name interface type namespaces]} interface-names interface-ns-name color-mode]
  (let [interface-name (:name interface)
        dependencies (deps/interface-ns-deps suffixed-top-ns interface-name interface-names (:src namespaces))]
    (mapcat #(error-message % name type interface-ns-name color-mode)
            (filterv #(not= interface-ns-name (get-namespace %)) dependencies))))

(defn errors [suffixed-top-ns interface-names components bases interface-ns-name color-mode]
  (vec (mapcat #(brick-errors suffixed-top-ns % interface-names interface-ns-name color-mode)
               (concat components bases))))

(ns polylith.clj.core.validator.m101-illegal-namespace-deps
  (:require [clojure.set :as set]
            [clojure.string :as str]
            [polylith.clj.core.common.interface :as common]
            [polylith.clj.core.deps.interface :as deps]
            [polylith.clj.core.util.interface :as util]
            [polylith.clj.core.util.interface.color :as color]))

(defn brick-error [{:keys [namespace depends-on-interface depends-on-ns]} brick-name type interface-ns color-mode]
  (when namespace
    (let [message (str "Illegal dependency on namespace " (color/namespc depends-on-interface depends-on-ns color-mode)
                       " in " (color/brick type brick-name color-mode) "." (color/namespc namespace color-mode)
                       ". Use " (color/namespc depends-on-interface interface-ns color-mode) " instead to fix the problem.")]
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
  "Checks for dependencies to component namespaces other than 'interface'
   for the 'src' context."
  [suffixed-top-ns {:keys [name interface type namespaces]} interface-names interface-ns color-mode]
  (let [interface-name (:name interface)
        dependencies (deps/interface-ns-deps suffixed-top-ns interface-name interface-names (:src namespaces))]
    (mapcat #(brick-error % name type interface-ns color-mode)
            (filterv #(not= interface-ns (get-namespace %))
                     dependencies))))

(defn import-ns [{:keys [namespace imports]}]
  (map #(vec [% namespace]) imports))

(defn component-ns [namespace suffixed-top-ns]
  (:depends-on-ns (common/extract-namespace suffixed-top-ns namespace)))

(defn component-error [component-name illegal-import ns->name suffixed-top-ns color-mode]
  (let [namespace (ns->name illegal-import)
        component-ns (:depends-on-ns (common/extract-namespace suffixed-top-ns namespace))
        {:keys [root-ns depends-on-ns]} (common/extract-namespace suffixed-top-ns illegal-import)
        message (str "Illegal dependency on namespace " (color/base root-ns color-mode) "." (color/namespc depends-on-ns color-mode)
                   " in " (color/component component-name color-mode) "." (color/namespc component-ns color-mode)
                   ". Components are not allowed to depend on bases.")]
    (util/ordered-map :type "error"
                      :code 101
                      :message (color/clean-colors message)
                      :colorized-message message
                      :bricks [component-name])))

(defn component-errors [{:keys [name namespaces]} base-namespaces suffixed-top-ns color-mode]
  (let [ns->name (into {} (mapcat import-ns
                                  (:src namespaces)))
        imports (-> ns->name keys set)
        illegal-imports (set/intersection imports base-namespaces)]
    (map #(component-error name % ns->name suffixed-top-ns color-mode)
         illegal-imports)))

(defn errors [suffixed-top-ns interface-names components bases interface-ns color-mode]
  (let [base-namespaces (set (common/entities-namespaces bases :src :test))]
    (vec (concat (mapcat #(brick-errors suffixed-top-ns % interface-names interface-ns color-mode)
                         (concat components bases))
                 (mapcat #(component-errors % base-namespaces suffixed-top-ns color-mode)
                         components)))))

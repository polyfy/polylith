(ns polylith.clj.core.validate.m101-illegal-namespace-deps
  (:require [polylith.clj.core.deps.interfc :as deps]
            [polylith.clj.core.util.interfc :as util]
            [polylith.clj.core.util.interfc.color :as color]
            [polylith.clj.core.common.interfc :as common]))

(defn error-message [{:keys [namespace depends-on-interface depends-on-ns]} brick-name type color-mode]
  (when namespace
    (let [message (str "Illegal dependency on namespace " depends-on-interface "." depends-on-ns
                       " in " brick-name "." namespace
                       ". Use " depends-on-interface ".interface instead to fix the problem.")
          colorized-msg (str "Illegal dependency on namespace " (color/namespc depends-on-interface depends-on-ns color-mode)
                             " in " (color/brick type brick-name color-mode) "." (color/namespc namespace color-mode)
                             ". Use " (color/namespc depends-on-interface "interface" color-mode) " instead to fix the problem.")]
      [(util/ordered-map :type "error"
                         :code 101
                         :message message
                         :colorized-message colorized-msg
                         :bricks [brick-name])])))

(defn brick-errors [{:keys [name top-namespace interface type namespaces-src]} interface-names color-mode]
  "Checks for dependencies to component interface namespaces other than 'interface'."
  (let [interface-name (:name interface)
        top-ns (common/top-namespace top-namespace)
        dependencies (deps/brick-dependencies top-ns interface-name interface-names namespaces-src)]
    (mapcat #(error-message % name type color-mode)
            (filterv #(-> % :depends-on-ns common/interface? not) dependencies))))

(defn errors [interface-names components bases color-mode]
  (vec (mapcat #(brick-errors % interface-names color-mode)
               (concat components bases))))

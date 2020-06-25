(ns polylith.validate.m101-illegal-namespace-deps
  (:require [polylith.deps.interface :as deps]
            [polylith.util.interface :as util]
            [polylith.common.interface.color :as color]))

(defn error-message [{:keys [namespace depends-on-interface depends-on-ns]} brick-name type dark-mode?]
  (when namespace
    (let [message (str "Illegal dependency on namespace " depends-on-interface "." depends-on-ns
                       " in " brick-name "." namespace
                       ". Use " depends-on-interface ".interface instead to fix the problem.")
          colorized-msg (str "Illegal dependency on namespace " (color/namespc depends-on-interface depends-on-ns dark-mode?)
                             " in " (color/brick type brick-name) "." (color/namespc namespace dark-mode?)
                             ". Use " (color/namespc depends-on-interface "interface" dark-mode?) " instead to fix the problem.")]
      [(util/ordered-map :type "error"
                         :code 101
                         :message message
                         :colorized-message colorized-msg
                         :bricks [brick-name])])))

(defn brick-errors [top-ns {:keys [name interface type namespaces-src]} interface-names dark-mode?]
  "Checks for dependencies to component interface namespaces other than 'interface'."
  (let [interface-name (:name interface)
        dependencies (deps/brick-dependencies top-ns interface-name interface-names namespaces-src)]
    (mapcat #(error-message % name type dark-mode?)
            (filterv #(not= "interface" (:depends-on-ns %)) dependencies))))

(defn errors [top-ns interface-names components bases dark-mode?]
  (vec (mapcat #(brick-errors top-ns % interface-names dark-mode?)
               (concat components bases))))

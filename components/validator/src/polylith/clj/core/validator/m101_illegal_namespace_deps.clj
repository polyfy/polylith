(ns ^:no-doc polylith.clj.core.validator.m101-illegal-namespace-deps
  (:require [polylith.clj.core.util.interface :as util]
            [polylith.clj.core.util.interface.color :as color]))

(defn brick-error [name type interface-ns {:keys [to-brick-id to-type to-namespace from-ns]} color-mode]
  (let [message (str "Illegal dependency on namespace " (color/brick to-type to-brick-id color-mode) "." (color/namespc to-namespace color-mode)
                     " in " (color/brick type name color-mode) "." (color/namespc from-ns color-mode)
                     (if (and (= "component" type)
                              (= "base" to-type))
                       ". Components are not allowed to depend on bases."
                       (if (= "base" to-type)
                         "."
                         (str ". Use " (color/namespc "component" to-brick-id interface-ns color-mode) " instead to fix the problem."))))]
    (util/ordered-map :type "error"
                      :code 101
                      :message (color/clean-colors message)
                      :colorized-message message
                      :bricks [name])))

(defn brick-errors [{:keys [name type illegal-deps]} interface-ns color-mode]
  (mapv #(brick-error name type interface-ns % color-mode)
        illegal-deps))

(defn errors [components bases interface-ns color-mode]
  (vec (mapcat #(brick-errors % interface-ns color-mode)
               (concat components bases))))

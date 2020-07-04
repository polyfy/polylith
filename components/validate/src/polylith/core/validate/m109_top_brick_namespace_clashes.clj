(ns polylith.core.validate.m109-top-brick-namespace-clashes
  (:require [polylith.core.util.interfc :as util]
            [polylith.core.util.interfc.color :as color]
            [clojure.string :as str]))

(defn no-namespace-found [brick-type brick-name base? component? settings-top-namespaces color-mode]
  (let [namespaces (str/join ", " settings-top-namespaces)
        message (str "No matching top namespace was found for " brick-name
                     ". Valid namespaces are: " namespaces)
        colorized-msg (str "No matching top namespace was found for " (color/brick brick-type brick-name color-mode)
                           ". Valid namespaces are: " namespaces)]
    [(util/ordered-map :type "error"
                       :code 109
                       :message message
                       :colorized-message colorized-msg
                       :top-namespaces []
                       :bases (when base? [brick-name])
                       :components (when component? [brick-name]))]))

(defn namespace-clash-found [brick-type brick-name top-namespaces base? component? color-mode]
  (let [namespaces (str/join ", " top-namespaces)
        message (str "Clashing top namespaces was found for " brick-name ": " namespaces)
        colorized-msg (str "Clashing top namespaces was found for " (color/brick brick-type brick-name color-mode) ": " namespaces)]
    [(util/ordered-map :type "error"
                       :code 109
                       :message message
                       :colorized-message colorized-msg
                       :top-namespaces top-namespaces
                       :bases (when base? [brick-name])
                       :components (when component? [brick-name]))]))

(defn brick-error [{:keys [name type top-namespace top-namespaces]} settings-top-namespaces color-mode]
  (let [cnt (count top-namespaces)
        base? (= "base" type)
        component? (= "component" type)]
    (if top-namespace
      []
      (cond
        (zero? cnt) (no-namespace-found type name base? component? settings-top-namespaces color-mode)
        (> cnt 1) (namespace-clash-found type name top-namespaces base? component? color-mode)
        :else []))))

(defn errors [components bases top-namespaces color-mode]
  (mapcat #(brick-error % top-namespaces color-mode) (concat components bases)))

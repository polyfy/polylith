(ns polylith.clj.core.common.message
  (:require [clojure.string :as str]
            [polylith.clj.core.util.interface.color :as color]))

(defn message [{:keys [code type colorized-message]} color-mode]
  (if (= type "error")
    (str (color/error color-mode "  Error " code ": ") colorized-message)
    (str (color/warning color-mode "  Warning " code ": ") colorized-message)))

(defn pretty-messages
  ([messages color-mode]
   (str/join "\n" (map #(message % color-mode) messages)))
  ([{:keys [settings messages]}]
   (let [color-mode (:color-mode settings color/none)]
     (pretty-messages messages color-mode))))

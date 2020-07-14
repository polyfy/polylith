(ns polylith.clj.core.common.message
  (:require [clojure.string :as str]
            [polylith.clj.core.util.interfc.color :as color]))

(defn message-without-colors [{:keys [code type message]}]
  (if (= type "error")
    (str "  Error " code ": " message)
    (str "  Warning " code ": " message)))

(defn messages-without-colors [{:keys [messages]}]
  (str/join "\n" (map message-without-colors messages)))

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

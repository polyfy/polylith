(ns ^:no-doc polylith.clj.core.ws-file.messages
  (:require [clojure.string :as str]))

(defn warning-206?
  "We have reused this code, and that's why we also check the message."
  [{:keys [code message]}]
  (and (= 206 code)
       (str/starts-with? message "This workspace is deprecated.")))

(defn clean-messages
  "The user can't migrate a file that is read from a file,
   so we choose to remove that message."
  [{:keys [messages] :as workspace}]
  (assoc workspace :messages
                   (filterv (complement warning-206?)
                            messages)))

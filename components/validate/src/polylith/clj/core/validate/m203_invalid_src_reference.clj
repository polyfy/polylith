(ns polylith.clj.core.validate.m203-invalid-src-reference
  (:require [clojure.string :as str]
            [polylith.clj.core.util.interfc :as util]
            [polylith.clj.core.util.interfc.color :as color]))

(defn quoted [string]
  (str "\"" string "\""))

(defn non-existing-paths-warning [env non-existing-paths color-mode]
  (let [paths (str/join ", " (map quoted non-existing-paths))
        message (str "Non-existing directories was found in deps.edn for the " env " environment and will be ignored: " paths)
        colorized-msg (str "Non-existing directories was found in deps.edn for the " (color/environment env color-mode) " environment and will be ignored: " (color/grey color-mode paths))]
    [(util/ordered-map :type "warning"
                       :code 203
                       :message message
                       :colorized-message colorized-msg
                       :environment env)]))

(defn env-warnings [{:keys [name missing-paths]} color-mode]
  (when (-> missing-paths empty? not)
    (non-existing-paths-warning name missing-paths color-mode)))

(defn warnings [environments color-mode]
  (mapcat #(env-warnings % color-mode)
          environments))

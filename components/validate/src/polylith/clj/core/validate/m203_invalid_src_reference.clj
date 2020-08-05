(ns polylith.clj.core.validate.m203-invalid-src-reference
  (:require [clojure.string :as str]
            [polylith.clj.core.file.interfc :as file]
            [polylith.clj.core.util.interfc :as util]
            [polylith.clj.core.util.interfc.str :as str-util]
            [polylith.clj.core.util.interfc.color :as color]))

(defn not-exists? [ws-dir path]
  (let [check-path (-> path
                       (str-util/skip-if-ends-with "/test")
                       (str-util/skip-if-ends-with "/resources"))]
    (not (file/exists (str ws-dir "/" check-path)))))

(defn quoted [string]
  (str "\"" string "\""))

(defn non-existing-paths-warning [env non-existing-paths color-mode]
  (let [paths (str/join ", " (map quoted non-existing-paths))
        message (str "Non-existing directories was found for the " env " environment and will be ignored: " paths)
        colorized-msg (str "Non-existing directories was found for the " (color/environment env color-mode) " environment and will be ignored: " (color/grey color-mode paths))]
    [(util/ordered-map :type "warning"
                       :code 203
                       :message message
                       :colorized-message colorized-msg
                       :environment env)]))

(defn env-warnings [ws-dir color-mode {:keys [name src-paths test-paths]}]
  (let [paths (concat src-paths test-paths)
        non-existing-paths (filter #(not-exists? ws-dir %) paths)]
    (when (-> non-existing-paths empty? not)
      (non-existing-paths-warning name non-existing-paths color-mode))))

(defn warnings [ws-dir environments color-mode]
  (mapcat #(env-warnings ws-dir color-mode %)
          environments))

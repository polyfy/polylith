(ns polylith.clj.core.validator.m202-invalid-src-reference
  (:require [clojure.set :as set]
            [clojure.string :as str]
            [polylith.clj.core.util.interface :as util]
            [polylith.clj.core.util.interface.color :as color]))

(defn quoted [string]
  (str "\"" string "\""))

(defn non-existing-paths-warning [env non-existing-paths color-mode]
  (let [paths (str/join ", " (map quoted non-existing-paths))
        message (str "Non-existing directories was found in deps.edn for the " (color/environment env color-mode) " environment and will be ignored: " (color/grey color-mode paths))]
    [(util/ordered-map :type "warning"
                       :code 202
                       :message (color/clean-colors message)
                       :colorized-message message
                       :environment env)]))

(defn env-warnings [[env missing-paths] color-mode]
  (when (-> missing-paths empty? not)
    (non-existing-paths-warning env missing-paths color-mode)))

(defn missing-env-paths [{:keys [name src-paths]} missing-paths]
  [name (set/intersection (set src-paths)
                          (set missing-paths))])

(defn exclude-path [path]
  (and (not (str/ends-with? path "/test"))
       (not (str/ends-with? path "/resources"))))

(defn warnings [environments {:keys [missing]} color-mode]
  (let [missing-paths (filter exclude-path missing)]
    (mapcat #(env-warnings % color-mode)
            (map #(missing-env-paths % missing-paths)
                 environments))))

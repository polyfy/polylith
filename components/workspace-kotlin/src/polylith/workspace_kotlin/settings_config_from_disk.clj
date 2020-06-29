(ns polylith.workspace-kotlin.settings-config-from-disk
  (:require [clojure.string :as str]))

(defn config-row? [row]
  (str/starts-with? row "polylith."))

(defn as-value [value]
  (cond
    (and (str/starts-with? value "\"")
         (str/ends-with? value "\"")) (subs value 1 (dec (count value)))
    (or (= "false" value)
        (= "true" value)) (boolean value)
    :else value))

(defn key-value [row]
  (let [var-index (str/index-of row "=")]
    [(keyword (str/trim (subs row 9 var-index)))
     (as-value (str/trim (subs row (inc var-index))))]))

(defn settings-from-disk [ws-path]
  (let [config-path (str ws-path "/settings.gradle.kts")
        rows (str/split-lines (slurp config-path))]
    (into {} (map key-value
                  (filter config-row? rows)))))

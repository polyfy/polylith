(ns polylith.clj.core.ws-updater.interface
  (:import (clojure.lang Keyword Symbol)))

(def type->caster {Keyword keyword
                   Boolean #(Boolean/parseBoolean %)
                   Long    #(Long/parseLong %)
                   Symbol  symbol})

(def string-type->caster {"boolean" #(Boolean/parseBoolean %)
                          "long"    #(Long/parseLong %)
                          "keyword" keyword
                          "symbol"  symbol})

(defn caster [type-value the-type]
  (if the-type
    (string-type->caster the-type str)
    (type->caster (type type-value) str)))

(defn single-type [type]
  (condp = type
    "booleans" ["boolean" true]
    "longs" ["long" true]
    "strings" ["string" true]
    "keywords" ["keyword" true]
    "symbols" ["symbol" true]
    [type false]))

(defn values? [value]
  (and (seqable? value)
       (not (string? value))))

(defn set-value
  "Sets a value in the workspace, e.g.:
    key:configs:workspace:validations:inconsistent-lib-versions:type value:error

    If a value already exists, the original type will be used (by casting it from a string),
    otherwise it will be set to a string or vector of strings (if more than one value),
    or use the provided type to set the value to that type(s)."
  [workspace set-path type value]
  (let [path (mapv keyword set-path)
        existing-value (get-in workspace path)
        type-value (if (values? existing-value)
                     (first existing-value)
                     existing-value)
        [type put-in-vector?] (single-type type)
        type-caster (caster type-value type)
        new-value (if (values? value)
                    (mapv #(type-caster %) value)
                    (if put-in-vector?
                      [(type-caster value)]
                      (type-caster value)))]
    (assoc-in workspace path new-value)))

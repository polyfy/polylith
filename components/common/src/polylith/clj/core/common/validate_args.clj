(ns polylith.clj.core.common.validate-args
  (:require [clojure.string :as str]))

(defn not-profile-or-empty? [arg]
  (and (not= "" arg)
       (not (str/starts-with? arg "+"))))

(defn validate [unnamed-args example]
  (if (empty? (filter not-profile-or-empty? unnamed-args))
    {:ok? true}
    {:message (str "  Arguments should be passed by name, e.g.: " example)}))

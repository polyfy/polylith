(ns polylith.spec.core
  (:require [clojure.spec.alpha :as s]
            [clojure.string :as str]))

(s/def ::not-blank-string #(not (str/blank? %)))

(s/def ::top-namespace ::not-blank-string)

(s/def ::env-prefix ::not-blank-string)

(s/def ::config (s/keys :req-un [::top-namespace]
                        :opt-un [::env-prefix]))

(defn valid-config? [config]
  (s/valid? ::config config))

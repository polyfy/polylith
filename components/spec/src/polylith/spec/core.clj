(ns polylith.spec.core
  (:require [clojure.spec.alpha :as s]
            [clojure.string :as str]))

(s/def ::not-blank-string #(not (str/blank? %)))

(s/def ::compile-path (s/or :nil? nil?
                            :string ::not-blank-string))

(s/def ::top-namespace ::not-blank-string)

(s/def ::thread-pool-size (s/or :nil? nil?
                                :int  (s/and int? #(< 0 %))))

(s/def ::config (s/keys :req-un [::top-namespace]
                        :opt-un [::compile-path
                                 ::thread-pool-size]))

(defn valid-config? [config]
  (s/valid? ::config config))

(ns ^:no-doc polylith.clj.core.util.interface.dialects)

(def valid-dialects #{"clj" "cljs"})

(defn valid-dialect? [dialect]
  (contains? valid-dialects dialect))

(defn clean-dialects [dialects]
  (let [valid-dialects (if (coll? dialects)
                         (->> dialects (filter valid-dialect?) (into #{}))
                         #{})]
    (if (empty? valid-dialects) 
      #{"clj"}
      valid-dialects)))

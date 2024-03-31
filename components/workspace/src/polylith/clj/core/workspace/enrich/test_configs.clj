(ns polylith.clj.core.workspace.enrich.test-configs)

(declare merge-data)

(defn merge-key [result [[k _] value ks]]
  (merge-data result [value (conj ks k)]))

(defn merge-maps
  "Iterate through all keys in the m hash map, and merge everything into result."
  [result value m ks]
  (reduce merge-key result
          (map vector m
                      (repeat value)
                      (repeat ks))))

(defn merge-vector [result v1 v2 ks]
  (assoc-in result ks
            (vec (distinct (into v1 v2)))))

(defn merge-data
  "result is the initial value (the value of :test) and the value is the
   config we want to merge into result. ks stores the key(s) we are merging
   from value to result, which is initially set to [], and then grows when (if)
   we get deeper into the value hash map, e.g. [:key1 :key2]."
  [result [value ks]]
  (let [v1 (get-in result ks)
        v2 (get-in value ks)]
    (cond
      (and (map? v1)
           (map? v2)) (merge-maps result value v2 ks)
      (and (vector? v1)
           (vector? v2)) (merge-vector result v1 v2 ks)
      :else (assoc-in result ks v2))))

(defn with-configs
  "Merge snippets of test configuration into the global test configuration.
   The snippets are configured in :test-configs, and are selected with e.g.
   merge:snippet1:snippet2, passed in as user-input arguments."
  [settings test configs {:keys [with]}]
  (let [test-configs (-> configs :workspace :test-configs)
        enriched-test (reduce merge-data (or test {})
                              (mapv vector (remove nil?
                                                   (mapv test-configs (map keyword with)))
                                           (repeat (vector))))]
    (cond-> settings
            (seq enriched-test) (assoc :test enriched-test))))

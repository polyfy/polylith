(ns polylith.clj.core.doc.navigation.calculator)

(defn include-key? [[key data]]
  (and (not= :url key)
       (-> data :exclude-from-navigation? not)))

(defn ->navigation [key config]
  (let [config-keys (keys (filter include-key? config))]
    (if (empty? config-keys)
      [key []]
      [key (into {} (map #(->navigation % (config %))
                         config-keys))])))

(defn navigation [config]
  (second (->navigation :root config)))

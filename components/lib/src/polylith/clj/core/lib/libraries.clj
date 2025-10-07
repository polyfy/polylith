(ns ^:no-doc polylith.clj.core.lib.libraries)

(defn lib [[name {:keys [version size type]}]]
  [(cond-> {:name name
            :version (or version "-")}
           size (assoc :size size)
           type (assoc :type type))])

(defn entity-lib [[name {:keys [brick] :as entity}]]
  (if (nil? brick)
    (lib [name entity])
    []))

(defn profile-lib [{:keys [lib-deps]}]
  (mapcat entity-lib lib-deps))

(defn libs [{:keys [profiles components bases projects]}]
  (let [entities (concat components bases projects)
        src-libs (set (concat (mapcat entity-lib (mapcat #(-> % :lib-deps :src) entities))
                              (mapcat profile-lib profiles)))
        test-libs (set (mapcat lib (mapcat #(-> % :lib-deps :test) entities)))
        libs (sort-by (juxt :name :version)
                      (set (concat src-libs test-libs)))]
    {:libraries libs
     :src-libs src-libs}))

(defn used-libraries [workspace]
  (sort (set (map :name
                  (:libraries (libs workspace))))))


(comment
  (require '[dev.jocke :as dev])
  (:libraries (libs dev/workspace))
  (def filtered-libs ["kibu/pushy"])
  #__)

(ns ^:no-doc polylith.clj.core.info.table.ws-column.external.brick)

(defn lib-brick [[_ {:keys [brick]}]]
  brick)

(defn project-brick [{:keys [lib-deps]}]
  (concat (filter identity (map lib-brick (:src lib-deps)))
          (filter identity (map lib-brick (:test lib-deps)))))

(defn profile-brick [{:keys [lib-deps]}]
  (filter identity (map lib-brick lib-deps)))

(defn entity-bricks [entities entity-fn]
  (let [bricks (sort-by (juxt (complement :type) :alias :name)
                        (set (mapcat entity-fn entities)))]
    [(filter #(= :base (:type %)) bricks)
     (filter #(= :component (:type %)) bricks)]))

(defn profile-bricks [profiles]
  (entity-bricks profiles profile-brick))

(defn project-bricks [projects]
  (entity-bricks projects project-brick))

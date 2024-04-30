(ns ^:no-doc polylith.clj.core.info.table.ws-column.external.brick)

(defn lib-brick [[_ {:keys [brick]}]]
  brick)

(defn project-bricks [{:keys [lib-deps]}]
  (concat (filter identity (map lib-brick (:src lib-deps)))
          (filter identity (map lib-brick (:test lib-deps)))))

(defn profile-bricks [{:keys [lib-deps]}]
  (filter identity (map lib-brick lib-deps)))

(defn bricks [projects profiles]
  (let [bricks (sort-by (juxt (complement :type) :alias :name)
                        (set (concat (mapcat project-bricks projects)
                                     (mapcat profile-bricks profiles))))]
    [(filter #(= :base (:type %)) bricks)
     (filter #(= :component (:type %)) bricks)]))

(ns polylith.clj.core.validator.suppress)

(defn suppress? [{:keys [warning error bricks projects]} number {:keys [type name]}]
  (and (or (= number warning)
           (= number error))
       (or (and (contains? #{"component" "base"} type)
                (or (not bricks)
                    (contains? (set bricks) name)))
           (and (= type "project")
                (or (not projects)
                    (contains? (set projects) name))))))

(defn suppress-any? [disable validation-number entity]
  (some #(suppress? % validation-number entity)
        disable))

(defn suppress [disable validation-number entities]
  (keep #(when-not (suppress-any? disable validation-number %) %)
        entities))

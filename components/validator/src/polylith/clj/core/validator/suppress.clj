(ns polylith.clj.core.validator.suppress)

(defn suppress?
  "When neither :bricks nor :projects is given, the validation is suppressed for all entities.
   When one of them is given, only the listed entities of that kind are suppressed."
  [{:keys [warning error bricks projects]} number {:keys [type name]}]
  (and (or (= number warning)
           (= number error))
       (or (and (contains? #{"component" "base"} type)
                (or (and (not bricks)
                         (not projects))
                    (contains? (set bricks) name)))
           (and (= type "project")
                (or (and (not bricks)
                         (not projects))
                    (contains? (set projects) name))))))

(defn suppress-any? [disable validation-number entity]
  (some #(suppress? % validation-number entity)
        disable))

(defn suppress [disable validation-number entities]
  (keep #(when-not (suppress-any? disable validation-number %) %)
        entities))

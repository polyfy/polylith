(ns ^:no-doc polylith.clj.core.validator.suppress)

(defn suppress?
  "Only warnings can be suppressed. When neither :bricks nor :projects is given, the
   warning is suppressed for all entities. When one of them is given, only the listed
   entities of that kind are suppressed."
  [{:keys [warning bricks projects]} number {:keys [type name]}]
  (and (= number warning)
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

(ns polylith.workspace.environment)

(defn matched? [{:keys [group test?]} env include-tests?]
  (and (= group env)
       (or include-tests?
           (not test?))))

(defn select [environments env include-tests?]
  (filterv #(matched? % env include-tests?) environments))

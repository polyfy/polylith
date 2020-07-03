(ns polylith.core.workspace.environment)

(defn matched? [{:keys [group test?]} env-group include-tests?]
  (and (= group env-group)
       (or include-tests?
           (not test?))))

(defn select [environments env-group include-tests?]
  (filterv #(matched? % env-group include-tests?) environments))

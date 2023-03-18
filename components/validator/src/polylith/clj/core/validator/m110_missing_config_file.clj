(ns polylith.clj.core.validator.m110-missing-config-file)

(defn error [{:keys [error]}]
  {:type "error"
   :code 110
   :message error
   :colorized-message error})

(defn errors [errors]
  (mapv error errors))

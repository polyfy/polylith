(ns polylith.clj.core.validator.m301-inconsistent-lib-versions
  (:require [clojure.string :as str]
            [polylith.clj.core.util.interface :as util]
            [polylith.clj.core.util.interface.color :as color]))

(defn library-message [[lib-name libraries] type]
  (let [versions (str/join ", " (mapv :version libraries))
        message (str "Inconsistent versions detected for library '" lib-name "': " versions)]
    (when (and (keyword? type)
               (not-empty libraries))
      [(util/ordered-map :type (name type)
                         :code 301
                         :message (color/clean-colors message)
                         :colorized-message message)])))

(defn messages
  [configs libraries]
  ;; Returns warnings or errors (warning if :type = :warning, or error if :type = :error)
  (let [type (-> configs :workspace :validations :inconsistent-lib-versions :type)
        libraries (group-by :name
                            (filter :inconsistent-lib-version libraries))]
    (mapcat #(library-message % type)
            libraries)))

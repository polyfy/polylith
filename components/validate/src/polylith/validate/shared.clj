(ns polylith.validate.shared
  (:require [clojure.string :as str]))

(defn full-name
  ([{:keys [sub-ns name]}]
   (full-name sub-ns name))
  ([sub-ns name]
   (if (str/blank? sub-ns)
     name
     (str sub-ns "/" name))))

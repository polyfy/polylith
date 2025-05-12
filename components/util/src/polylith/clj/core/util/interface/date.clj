(ns polylith.clj.core.util.interface.date
  (:require [polylith.clj.core.util.date :as date]))

(defn parse-date [string]
  (date/parse-date string))

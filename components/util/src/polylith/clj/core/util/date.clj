(ns polylith.clj.core.util.date
  (:import [java.time Instant LocalDate ZoneId]
           [java.time.format DateTimeParseException]
           [java.util Date]))

(defn parse-date [string]
  (try
    (Date/from (Instant/parse string))
    (catch DateTimeParseException _
      (let [local-date (LocalDate/parse string)
            zoned (-> local-date
                      (.atStartOfDay (ZoneId/of "UTC"))
                      (.toInstant))]
        (Date/from zoned)))))

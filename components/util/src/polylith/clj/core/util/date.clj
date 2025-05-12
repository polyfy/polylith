(ns polylith.clj.core.util.date
  (:import [java.time Instant LocalDate Year YearMonth ZoneId]
           [java.time.format DateTimeFormatter DateTimeParseException]
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

(defn parse-date [string]
  (let [zone (ZoneId/of "UTC")]
    (try
      ;; Try full ISO-8601 first
      (Date/from (Instant/parse string))
      (catch Exception _
        (try
          ;; yyyy-MM-dd
          (let [formatter (DateTimeFormatter/ofPattern "yyyy-MM-dd")
                date (LocalDate/parse string formatter)]
            (Date/from (.toInstant (.atStartOfDay date zone))))
          (catch Exception _
            (try
              ;; yyyy-MM
              (let [formatter (DateTimeFormatter/ofPattern "yyyy-MM")
                    year-month (YearMonth/parse string formatter)
                    date (.atDay year-month 1)]
                (Date/from (.toInstant (.atStartOfDay date zone))))
              (catch Exception _
                (try
                  ;; yyyy
                  (let [formatter (DateTimeFormatter/ofPattern "yyyy")
                        year (Year/parse string formatter)
                        date (.atDay (.atMonth year 1) 1)]
                    (Date/from (.toInstant (.atStartOfDay date zone))))
                  (catch Exception _
                    (throw (ex-info "Unparseable inst value" {:value string}))))))))))))

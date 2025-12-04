#!/usr/bin/env bb

(require '[babashka.http-client :as http]
         '[clojure.string :as str])

(def versions
  ["0.3.31" "0.3.30" "0.3.0" "0.2.22" "0.2.21" "0.2.20"
   "0.2.19" "0.2.18" "0.2.17-alpha" "0.2.16-alpha"
   "0.2.15-alpha" "0.2.14-alpha" "0.2.13-alpha" "0.2.12-alpha"
   "0.2.0-alpha11" "0.2.0-alpha10" "0.1.0-alpha9" "0.1.0-alpha8"
   "0.1.0-alpha7" "0.1.0-alpha6" "0.1.0-alpha5" "0.1.0-alpha4"
   "0.1.0-alpha3" "0.1.0-alpha2" "0.1.0-alpha1"])

(defn get-downloads [version]
  (try
    (let [url (str "https://clojars.org/polylith/clj-poly/versions/" version)
          resp (http/get url)
          body (:body resp)
          m (re-find #"(\d[\d,]*)\s+This Version" body)]
      (if m
        (str/replace (second m) "," "")
        ""))
    (catch Exception e
      "-")))

(defn format-number [n]
  (let [s (str n)
        len (count s)
        parts (loop [i len
                     acc []]
                 (if (<= i 0)
                   acc
                   (let [start (max 0 (- i 3))
                         part (subs s start i)]
                     (recur start (cons part acc)))))]
    (str/join "," parts)))

(println "Fetching download statistics...")
(def data
  (for [v versions]
    (let [downloads (get-downloads v)]
      (Thread/sleep 200)  ;; Be nice to the server
      {:version v
       :downloads (if (and downloads (not= downloads "") (not= downloads "-"))
                    (parse-long downloads)
                    0)})))

(def version-width (max (count "Version")
                        (apply max (map #(count (:version %)) data))))
(def downloads-width (max (count "Downloads")
                          (apply max (map #(count (format-number (:downloads %))) data))))

(println)
(println (str (format (str "%-" version-width "s") "Version")
              " | "
              (format (str "%" downloads-width "s") "Downloads")))
(println (str (str/join (repeat (+ version-width 3 downloads-width) "-"))))
(doseq [{:keys [version downloads]} data]
  (println (str (format (str "%-" version-width "s") version)
                " | "
                (format (str "%" downloads-width "s") (format-number downloads)))))

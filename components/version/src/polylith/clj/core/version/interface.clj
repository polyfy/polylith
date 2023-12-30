(ns ^:no-doc polylith.clj.core.version.interface
  (:refer-clojure :exclude [name])
  (:require [clojure.string :as str]
            [polylith.clj.core.system.interface :as system]))

;; -----------------------------------------------------------------------------
;; If a final release:
;;  - build and release the 'poly' tool to https://github.com/polyfy/polylith.
;;  - build and release the 'clj-poly' library to https://clojars.org.
;;  - release the 'clj-poly-NAME' documentation to cljdoc.org
;;    where NAME is specified by 'name' in this namespace
;;    (triggered when 'clj-poly' is pushed to Clojars).
;;
;; If a SNAPSHOT release:
;;  - make a pre-release of the 'poly' tool to https://github.com/polyfy/polylith.
;;  - build and release the 'clj-poly' library to https://clojars.org.
;;  - release the 'clj-poly-SNAPSHOT' documentation to cljdoc.org
;;    (triggered when 'clj-poly' is pushed to Clojars).
;; -----------------------------------------------------------------------------

(def RELEASE "")
(def SNAPSHOT "SNAPSHOT")

(def major 0)
(def minor 2)
(def patch 19)
(def revision SNAPSHOT) ;; Set to SNAPSHOT or RELEASE.
(def snapshot 1) ;; Increase by one for every snapshot release, or set to 0 if a release.
(def snapshot? (= SNAPSHOT revision))

(def name-without-rev (str major "." minor "." patch))
(def name (str name-without-rev
               (if (str/blank? revision)
                 ""
                 (str "-" revision))))

(def tool (if system/extended? "polyx" "poly"))

(def date "2023-12-30")

;; Execute 'poly doc version' to see when different changes was introduced.
(def api-version {:breaking 1, :non-breaking 0})
(def test-runner-api-version {:breaking 1, :non-breaking 0})
(def ws-api-version {:breaking 2, :non-breaking 0})

(defn version
  ([]
   (version nil))
  ([from-version]
   (cond-> {:release {:name name
                      :tool tool
                      :major major
                      :minor minor
                      :patch patch
                      :revision revision
                      :date date}
            :api api-version
            :test-runner-api test-runner-api-version
            :ws ws-api-version}
           from-version (assoc :from from-version)
           snapshot? (assoc-in [:release :snapshot] snapshot))))

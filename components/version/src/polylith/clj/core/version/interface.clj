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
(def minor 3)
(def patch 33)
(def revision SNAPSHOT) ;; Set to SNAPSHOT or RELEASE.
(def snapshot 0) ;; Increase by one for every snapshot release, or set to 0 if a release.
                  ;; Also update :snapshot-version: at the top of readme.adoc.
(def snapshot? (= SNAPSHOT revision))

(def name-without-rev (str major "." minor "." patch))
(def name (str name-without-rev
               (if (str/blank? revision)
                 ""
                 (str "-" revision))))

(def tool (if system/extended? "polyx" "poly"))

(def date "2025-12-29")

;; Execute 'poly doc page:versions' to see when different changes was introduced.
(def api-version {:breaking 1, :non-breaking 0})
(def test-runner-api-version {:breaking 1, :non-breaking 0})
(def workspace-version {:breaking 5, :non-breaking 0})

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
            :ws workspace-version}
           from-version (assoc :from from-version)
           snapshot? (assoc-in [:release :snapshot] snapshot))))

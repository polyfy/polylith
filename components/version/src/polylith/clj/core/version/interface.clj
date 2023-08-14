(ns ^:no-doc polylith.clj.core.version.interface
  (:refer-clojure :exclude [name])
  (:require [clojure.string :as str]
            [polylith.clj.core.system.interface :as system]))

;; ------------------------------------------------------------------------
;; - if a final release:
;;   - set revision to "", and snapshot to 0.
;;
;; - if working towards a release:
;;   - if we have just released a version, and snapshot is 0,
;;     set revision to "SNAPSHOT" and snapshot to 1.
;;   - for all subsequent snapshot releases, increase snapshot by 1.
;;
;; If a snapshot release, we will release a clj-poly library to Clojars,
;; which will trigger a new build of the cljdoc documentation.
;; If a final release, we will also build and deploy the poly tool to github.
;; ------------------------------------------------------------------------

(def major 0)
(def minor 2)
(def patch 18)
(def revision "SNAPSHOT")
(def snapshot 1)
(def snapshot? (= "SNAPSHOT" revision))

(def name-without-rev (str major "." minor "." patch))
(def name (str name-without-rev
               (if (str/blank? revision)
                 ""
                 (str "-" revision))))

(def tool (if system/extended? "polyx" "poly"))

(def date "2023-08-14")

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
            :test-runner-api {:breaking 1
                              :non-breaking 0}
            :ws {:type :toolsdeps2
                 :breaking 2
                 :non-breaking 0}}
           from-version (assoc :from from-version)
           (= "SNAPSHOT" revision) (assoc-in [:release :snapshot] snapshot))))

;; Definition of a breaking change in the workspace structure is:
;; - when an attribute changes name.
;; - when the shape of the data for an attribute changes.
;; - when an attribute is deleted.
;; All other changes are non-breaking changes.

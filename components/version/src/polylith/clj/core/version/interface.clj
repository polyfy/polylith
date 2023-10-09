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
;;  - build and release the 'clj-poly' library to https://clojars.org.
;;  - release the 'clj-poly-SNAPSHOT' documentation to cljdoc.org
;;    (triggered when 'clj-poly' is pushed to Clojars).
;; -----------------------------------------------------------------------------

(def RELEASE "")
(def SNAPSHOT "SNAPSHOT")

(def major 0)
(def minor 2)
(def patch 18)
(def revision SNAPSHOT) ;; Set to SNAPSHOT or RELEASE.
(def snapshot 4) ;; Increase by one for every snapshot release, or set to 0 if a release.
(def snapshot? (= SNAPSHOT revision))

(def name-without-rev (str major "." minor "." patch))
(def name (str name-without-rev
               (if (str/blank? revision)
                 ""
                 (str "-" revision))))

(def tool (if system/extended? "polyx" "poly"))

(def date "2023-10-07")

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

;; ====== Workspace attributes (ws) ======
;;
;; ws     release         action    attribute                                                  Description
;; -----  -------------   -------   -----------------------------------------------------------------------------------------------------------------------
;; 2.0    0.2.18          added     configs
;;                        added     bases:BASE:base-deps
;;                        deleted   version.ws.type                                            Moved out to ws-type.
;;                        deleted   version.from.ws.type
;;                        added     version:api
;;                        added     version:tool
;;                        added     version:test-runner-api
;;                        added     version:release:snapshot
;;                        added     ws-type
;;                        renamed   interfaces:definitions:DEFINITION:arglist                  Renamed from parameters.
;;                        renamed   projects:PROJECT:deps:BRICK:SOURCE:missing-ifc-and-bases   Renamed from missing-ifc.
;;                        added     ENTITIES:ENTITY:namespaces:SOURCE:NAMESPACE:is-ignored
;;                        added     ENTITIES:ENTITY:namespaces:SOURCE:NAMESPACE:is-invalid
;;                        changed   ENTITIES:ENTITY:namespaces:SOURCE:NAMESPACE:file-path      The ws-dir part is removed from the file path (data change).
;; 1.2    0.2.16-alpha    added     ENTITIES:ENTITY:namespaces:src:NAMESPACE:invalid
;; 1.1    0.2.14-alpha    added     settings:vcs:is-git-repo
;;                        deleted   projects:PROJECT:is-run-tests
;;
;; 0.0    0.2.0-alpha9    Version 0.2.0-alpha9 and earlier, has from.ws set to 0.0 if read from file.
;;
;; ENTITIES = bases, components, or projects.
;; ENTITY = a base, component, or project name.
;; NAMESPACE = a namespace name
;; SOURCE = src or test.

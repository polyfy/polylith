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
;; If a SNAPSHOT release, we will release a clj-poly library to Clojars,
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
           (= "SNAPSHOT" revision) (assoc-in [:release :snapshot] snapshot))))

;; ====== Workspace attributes (ws) ======
;;
;; ws     release         action    attribute                                                  Description
;; -----  -------------   -------   -----------------------------------------------------------------------------------------------------------------------
;; 2.0    0.2.18          added     configs
;;                        added     bases:BASE:base-deps
;;                        deleted   version.ws.type                                            Moved out to ws-type.
;;                        added     version:api
;;                        added     version:tool
;;                        added     version:test-runner-api
;;                        added     version:release:snapshot
;;                        added     ws-type
;;                        renamed   projects:PROJECT:deps:BRICK:SOURCE:missing-ifc-and-bases   Renamed from missing-ifc.
;;                        added     ENTITIES:ENTITY:namespaces:SOURCE:NAMESPACE:is-ignored
;;                        added     ENTITIES:ENTITY:namespaces:SOURCE:NAMESPACE:is-invalid
;;                        changed   ENTITIES:ENTITY:namespaces:SOURCE:NAMESPACE:file-path      The ws-dir part is removed from the file path (data change).
;; 1.2    0.2.16-alpha    added     ENTITIES:ENTITY:namespaces:src:NAMESPACE:invalid
;; 1.1    0.2.14-alpha    added     settings:vcs:is-git-repo
;;                        deleted   projects:PROJECT:is-run-tests
;;
;; ENTITIES = bases, components, or projects.
;; ENTITY = a base, component, or project name.
;; NAMESPACE = a namespace name
;; SOURCE = src or test.

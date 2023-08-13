(ns ^:no-doc polylith.clj.core.version.interface
  (:refer-clojure :exclude [name])
  (:require [clojure.string :as str]
            [polylith.clj.core.system.interface :as system]))

;; ------------------------------------------------------------------------
;; - if a release: set revision to "" and leave snapshot as it is.
;; - if working towards a release candidate (or a release fix):
;;   set revision to "SNAPSHOT", and increment snapshot to e.g. 1.
;;
;; Note that revision should only be set to "SNAPSHOT" or "" (if a release).
;; If set to "SNAPSHOT" we will release a clj-poly library to Clojars,
;; which will trigger a new build of the cljdoc documentation.
;; An AOT compiled poly tool is only built and released to github if a release,
;; not if it's a SNAPSHOT.
;; ------------------------------------------------------------------------

(def major 0)
(def minor 2)
(def patch 18)
(def revision "SNAPSHOT")
(def snapshot 1)

(def name-without-rev (str major "." minor "." patch))
(def name (str name-without-rev
               (if (str/blank? revision)
                 ""
                 (str "-" revision))))

(def tool (if system/extended? "polyx" "poly"))

(def date "2023-08-02")

(defn version
  ([]
   (version nil))
  ([from-version]
   (cond-> {:release {:name     name
                      :tool     tool
                      :major    major
                      :minor    minor
                      :patch    patch
                      :revision revision
                      :snapshot snapshot
                      :date     date}
            :test-runner-api {:breaking 1
                              :non-breaking 0}
            :ws {:type :toolsdeps2
                 :breaking 2
                 :non-breaking 0}}
           from-version (assoc :from from-version))))

;; ====== Workspace attributes (ws) ======
;;
;; ws     release         action    attribute                                                  Description
;; -----  -------------   -------   ------------------------------------------------------------------------------------------------------
;; 2.0    0.2.18          added     configs
;;                        added     bases:BASE:base-deps
;;                        added     version:test-runner-api
;;                        added     version:tool
;;                        added     ENTITIES:ENTITY:namespaces:SOURCE:0:is-ignored
;;                        added     ENTITIES:ENTITY:namespaces:SOURCE:0:is-invalid
;;                        changed   ENTITIES:ENTITY:namespaces:SOURCE:0:file-path              The ws-dir part is removed from the file path.
;;                        added     projects:PROJECT:deps:BRICK:SOURCE:missing-ifc-and-bases   Renamed from missing-ifc.
;;                        added     ws-type
;; 1.2    0.2.16-alpha    added     ENTITIES:ENTITY:namespaces:src:0:invalid
;; 1.1    0.2.14-alpha    added     settings:vcs:is-git-repo
;;                        deleted   projects:PROJECT:is-run-tests
;;
;; 0 = vector element.
;; ENTITIES = bases, components, or projects.
;; ENTITY = a base, component, or project name.
;; SOURCE = src or test.

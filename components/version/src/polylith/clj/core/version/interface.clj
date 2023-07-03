(ns polylith.clj.core.version.interface
  (:refer-clojure :exclude [name])
  (:require [clojure.string :as str]
            [polylith.clj.core.system.interface :as sys]))

(def system (if sys/admin-tool? "polyx" "poly"))
(def major 0)
(def minor 2)
(def patch 18)
(def revision "issue205-01")
(def name (str system "-"
               major "." minor "." patch
               (if (str/blank? revision)
                 ""
                 (str "-" revision))))

(def date "2023-06-24")

(defn version
  ([]
   (version nil))
  ([from-version]
   (cond-> {:release {:name name
                      :system system
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
           from-version (assoc :from from-version))))

;; ====== Workspace attributes (ws) ======
;;
;; ws     release         action    attribute                                                  Description
;; -----  -------------   -------   ------------------------------------------------------------------------------------------------------
;; 2.0    0.2.18          added     configs
;;                        added     bases:BASE:base-deps
;;                        added     entities:ENTITY:namespaces:source:0:is-ignored
;;                        added     entities:ENTITY:namespaces:source:0:is-invalid
;;                        changed   entities:ENTITY:namespaces:source:0:file-path              The ws-dir part is removed from the file path.
;;                        added     projects:PROJECT:deps:BRICK:source:missing-ifc-and-bases   Renamed from missing-ifc.
;;                        added     ws-type
;; 1.2    0.2.16-alpha    added     entities:ENTITY:namespaces:src:0:invalid
;; 1.1    0.2.14-alpha    added     settings:vcs:is-git-repo
;;                        deleted   projects:PROJECT:is-run-tests
;;
;; 0 = vector element.
;; entities = bases, components, or projects.
;; ENTITY = a base, component, or project name.
;; source = src or test.

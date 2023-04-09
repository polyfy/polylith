(ns polylith.clj.core.version.interface
  (:refer-clojure :exclude [name])
  (:require [clojure.string :as str]))

(def major 0)
(def minor 2)
(def patch 18)
(def revision "issue187-02")
(def name (str major "." minor "." patch
               (if (str/blank? revision)
                 ""
                 (str "-" revision))))

(def date "2023-05-18")

(def ws-breaking 2)
(def ws-non-breaking 0)

(defn version
  ([ws-type]
   (version {:type ws-type} nil ws-breaking ws-non-breaking))
  ([{:keys [type] :as from-ws} from-release-name ws-breaking ws-non-breaking]
   (let [from (when (not= :toolsdeps2 type)
                (cond-> {:ws from-ws}
                        from-release-name (assoc :release-name from-release-name)))]
     (cond-> {:release {:name name
                        :major major
                        :minor minor
                        :patch patch
                        :revision revision
                        :date date}
              :ws {:type :toolsdeps2
                   :breaking ws-breaking
                   :non-breaking ws-non-breaking}}
             from (assoc :from from)))))

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
;; ENTITY = an entity name.
;; source = src or test.

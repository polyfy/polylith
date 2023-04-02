(ns polylith.clj.core.version.interface
  (:refer-clojure :exclude [name])
  (:require [clojure.string :as str]))

(def major 0)
(def minor 2)
(def patch 18)
(def revision "issue293-01")
(def name (str major "." minor "." patch
               (if (str/blank? revision)
                 ""
                 (str "-" revision))))

(def date "2023-04-02")

(defn version
  ([ws-type]
   (version {:type ws-type} nil))
  ([{:keys [type] :as from-ws} from-release-name]
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
                   :breaking 2
                   :non-breaking 0}}
             from (assoc :from from)))))

;; ====== Workspace attributes (ws) ======
;;
;; ws     release         action    attribute                                           Description
;; -----  -------------   -------   ----------------------------------------------------------------------------------------------
;; 2.0    0.2.18          added     configs
;;                        added     ws-type
;;                        added     entities:ENTITY:namespaces:source:0:is-ignored
;;                        added     entities:ENTITY:namespaces:source:0:is-invalid
;;                        changed   entities:ENTITY:namespaces:source:0:file-path       ws-dir is no longer part of the file path.
;; 1.2    0.2.16-alpha    added     entities:ENTITY:namespaces:src:0:invalid
;; 1.1    0.2.14-alpha    added     settings:vcs:is-git-repo
;;                        deleted   projects:PROJECT:is-run-tests
;;
;; 0 = vector element.
;; entities = bases, components, or projects.
;; ENTITY = an entity name.
;; source = src or test.

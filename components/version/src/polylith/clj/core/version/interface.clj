(ns polylith.clj.core.version.interface
  (:refer-clojure :exclude [name])
  (:require [clojure.string :as str]))

(def major 0)
(def minor 2)
(def patch 18)
(def revision "issue259-11")
(def name (str major "." minor "." patch
               (if (str/blank? revision)
                 ""
                 (str "-" revision))))

(def date "2023-03-29")

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

;; === workspace attributes (ws) ===
;;
;; ws     release         action    attribute                                                  Description
;; -----  -------------   -------   ---------------------------------------------------------------------------------
;; 2.0    0.2.18          added     configs
;;                        added     ws-type
;;                        added     bases:BASE:namespaces:src:0:is-ignored
;;                        added     bases:BASE:namespaces:src:0:is-invalid
;;                        added     bases:BASE:namespaces:test:0:is-ignored
;;                        added     bases:BASE:namespaces:test:0:is-invalid
;;                        added     components:COMPONENT:namespaces:src:0:is-ignored
;;                        added     components:COMPONENT:namespaces:src:0:is-invalid
;;                        added     components:COMPONENT:namespaces:test:0:is-ignored
;;                        added     components:COMPONENT:namespaces:test:0:is-invalid
;;                        added     projects:PROJECT:namespaces:src:0:is-ignored
;;                        added     projects:PROJECT:namespaces:src:0:is-invalid
;;                        added     projects:PROJECT:namespaces:test:0:is-ignored
;;                        added     projects:PROJECT:namespaces:test:0:is-invalid
;;                        changed   bases:BASE:namespaces:src:0:file-path                      Skipped ws-dir in file-path.
;;                        changed   bases:BASE:namespaces:test:0:file-path                     Skipped ws-dir in file-path.
;;                        changed   components:COMPONENT:namespaces:src:0:file-path            Skipped ws-dir in file-path.
;;                        changed   components:COMPONENT:namespaces:test:0:file-path           Skipped ws-dir in file-path.
;;                        changed   projects:PROJECT:namespaces:src:0:file-path                Skipped ws-dir in file-path.
;;                        changed   projects:PROJECT:namespaces:test:0:file-path               Skipped ws-dir in file-path.
;; 1.2    0.2.16-alpha    added     bases:BASE:namespaces:src:0:invalid
;;                        added     bases:BASE:namespaces:test:0:invalid
;;                        added     components:COMPONENT:namespaces:src:0:invalid
;;                        added     components:COMPONENT:namespaces:test:0:invalid
;;                        added     projects:PROJECT:namespaces:src:0:invalid
;;                        added     projects:PROJECT:namespaces:test:0:invalid
;; 1.1    0.2.14-alpha    added     settings:vcs:is-git-repo
;;                        deleted   projects:PROJECT:is-run-tests

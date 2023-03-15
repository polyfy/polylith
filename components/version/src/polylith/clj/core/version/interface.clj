(ns polylith.clj.core.version.interface
  (:refer-clojure :exclude [name])
  (:require [clojure.string :as str]))

(def major 0)
(def minor 2)
(def patch 18)
(def revision "issue259-01")
(def name (str major "." minor "." patch
               (if (str/blank? revision)
                 ""
                 (str "-" revision))))

(def date "2023-03-15")

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
                   :breaking 1
                   :non-breaking 3}}
             from (assoc :from from)))))

;; === workspace attributes (ws) ===
;;
;; ws     release         action    attribute
;; -----  -------------   -------   -----------------------------------------------------------------
;; 1.3    0.2.18          added     :configs
;                         added     :ws-type
;; 1.2    0.2.16-alpha    added     :bases > BASE > :namespaces > :src/test > [] > :invalid
;;                        added     :projects > PROJECT > :namespaces > :src/test > [] > :invalid
;;                        added     :components > COMPONENT > :namespaces > :src/test > [] > :invalid
;; 1.1    0.2.14-alpha    added     :settings > :vcs > :is-git-repo
;;                        deleted   :projects > PROJECT > :is-run-tests

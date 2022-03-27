(ns polylith.clj.core.version.interface
  (:refer-clojure :exclude [name]))

(def major 0)
(def minor 2)
(def patch 14)
(def revision "alpha-issue189-04")
(def name (str major "." minor "." patch "-" revision))

(def date "2022-03-23")

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
                   :non-breaking 1}}
             from (assoc :from from)))))

; ws     release         action    attribute
;; -----  -------------   ------    ------------------------------------
;; 1.1     0.2.14-alpha   added     :settings > :vcs > :is-git-repo
;                         deleted   :projects > PROJECT > :is-run-tests

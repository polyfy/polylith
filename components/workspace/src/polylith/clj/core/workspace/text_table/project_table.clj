(ns ^:no-doc polylith.clj.core.workspace.text-table.project-table
  (:require [clojure.set :as set]
            [polylith.clj.core.common.interface :as common]
            [polylith.clj.core.path-finder.interface.extract :as extract]
            [polylith.clj.core.path-finder.interface.status :as status]
            [polylith.clj.core.text-table.interface :as text-table]
            [polylith.clj.core.util.interface.color :as color]
            [polylith.clj.core.workspace.text-table.profile :as profile]))

(defn profile-cell [index project-name column is-show-resources path-entries]
  (let [flags (status/project-status-flags path-entries project-name is-show-resources)]
    (text-table/cell column (+ index 3) flags :purple :center)))

(defn profile-col [index {:keys [name] :as profile} disk-paths start-column projects is-show-resources]
  (let [column (+ start-column (* 2 index))
        path-entries (extract/from-profiles-paths disk-paths profile)]
    (concat [(text-table/cell column 1 name :purple :center :horizontal)]
            (map-indexed #(profile-cell %1 %2 column is-show-resources path-entries)
                         (map :name projects)))))
(defn profile-columns [disk-paths start-column projects profiles is-show-resources]
  (apply concat
         (map-indexed #(profile-col %1 %2 disk-paths start-column projects is-show-resources)
                      profiles)))

(defn project-cell [project project-key column row changed-projects affected-projects color-mode]
  (let [name (project-key project)
        changed (cond
                  (contains? changed-projects name) " *"
                  (contains? affected-projects name) " +"
                  :else "")
        project (str (color/project name color-mode)
                     changed)]
    (text-table/cell column row project)))

(defn project-column [projects {:keys [changed-projects changed-or-affected-projects]} header project-key column color-mode]
  (let [affected-projects (set/difference (set changed-or-affected-projects) (set changed-projects))]
    (concat [(text-table/cell column header)]
            (map-indexed #(project-cell %2 project-key column (+ %1 3) (set changed-projects) affected-projects color-mode)
                         projects))))

(defn status-cell [index {:keys [name paths projects-to-test]} disk-paths is-show-resources]
  (let [path-entries (extract/from-paths paths disk-paths)
        satus-flags (str (status/project-status-flags path-entries name is-show-resources)
                         (if (contains? (set projects-to-test) name) "x" "-"))]
    (text-table/cell 5 (+ index 3) satus-flags :purple :center)))

(defn status-column
  "The third '--x' column is marked if the project is marked to be tested from *any* project."
  [projects disk-paths is-show-resources]
  (concat [(text-table/cell 5 "status")]
          (map-indexed #(status-cell %1 %2 disk-paths is-show-resources)
                       projects)))

(defn dev-cell [index {:keys [name]} {:keys [projects-to-test]} path-entries is-show-resources]
  (let [status-flags (str (status/project-status-flags path-entries name is-show-resources)
                          (if (contains? (set projects-to-test) name) "x" "-"))]
    (text-table/cell 7 (+ index 3) status-flags :purple :center)))

(defn dev-column [projects is-show-resources]
  (let [development (common/find-project "development" projects)
        alias (get development :alias "dev")
        path-entries (extract/from-paths (:paths development) nil)]
    (concat [(text-table/cell 7 1 alias :purple)]
            (map-indexed #(dev-cell %1 %2 development path-entries is-show-resources)
                         projects))))

(defn loc-cell [index lines-of-code column thousand-separator]
  (text-table/number-cell column (+ index 3) lines-of-code :right thousand-separator))

(defn loc-columns [is-show-loc projects n#profiles thousand-separator total-col-src total-loc-test]
  (when is-show-loc
    (let [column1 (+ 9 (* 2 n#profiles))
          column2 (+ 2 column1)]
      (concat [(text-table/cell column1 1 "loc" :none :right)]
              (map-indexed #(loc-cell %1 %2 column1 thousand-separator) (map #(-> % :lines-of-code :src) projects))
              [(text-table/number-cell column1 (+ (count projects) 3) total-col-src :right thousand-separator)]
              [(text-table/cell column2 1 "(t)" :none :right)]
              (map-indexed #(loc-cell %1 %2 column2 thousand-separator) (map #(-> % :lines-of-code :test) projects))
              [(text-table/number-cell column2 (+ (count projects) 3) total-loc-test :right thousand-separator)]))))

(defn table [{:keys [settings projects changes profiles paths]} is-show-loc is-show-resources]
  (let [{:keys [color-mode thousand-separator]} settings
        n#dev (count (filter :is-dev projects))
        inactive-profiles (if (zero? n#dev) [] (profile/inactive-profiles settings profiles))
        n#profiles (count inactive-profiles)
        total-loc-src (apply + (filter identity (map #(-> % :lines-of-code :src) projects)))
        total-loc-test (apply + (filter identity (map #(-> % :lines-of-code :test) projects)))
        project-col (project-column projects changes "project" :name 1 color-mode)
        alias-col (project-column projects {} "alias" :alias 3 color-mode)
        status-col (status-column projects paths is-show-resources)
        dev-col (if (zero? n#dev) [] (dev-column projects is-show-resources))
        profile-cols (if (zero? n#dev) [] (profile-columns paths 9 projects inactive-profiles is-show-resources))
        loc-col (loc-columns is-show-loc projects n#profiles thousand-separator total-loc-src total-loc-test)
        space-columns (range 2 (* 2 (+ 3 n#dev n#profiles (if is-show-loc 2 0))) 2)
        header-spaces (text-table/spaces 1 space-columns (repeat "  "))
        cells (text-table/merge-cells project-col alias-col status-col dev-col loc-col profile-cols header-spaces)
        section-cols (if (or is-show-loc (-> n#profiles zero? not)) [6 (+ 8 (* 2 n#profiles))] (if (zero? n#dev) [] [6]))
        line-spaces (text-table/spaces 2 section-cols (repeat "   "))
        line (text-table/line 2 cells)]
    (text-table/table "  " color-mode cells line line-spaces)))

(defn print-table [workspace is-show-loc is-show-resources]
  (text-table/print-table (table workspace is-show-loc is-show-resources)))

(comment
  (print-table dev.jocke/workspace false false)

  (require '[polylith.clj.core.common.interface :as common])
  (def project (common/find-project "cl" (:projects dev/workspace)))
  (:changes dev/workspace)
  (def projects (:projects dev/workspace))

  (into {} (map (juxt :name :projects-to-test) projects))

  #__)

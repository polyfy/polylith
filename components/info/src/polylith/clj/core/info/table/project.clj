(ns ^:no-doc polylith.clj.core.info.table.project
  (:require [clojure.set :as set]
            [polylith.clj.core.info.table.profile :as profile]
            [polylith.clj.core.common.interface :as common]
            [polylith.clj.core.info.table.brick.dialect-column :as dialect-column]
            [polylith.clj.core.path-finder.interface.extract :as extract]
            [polylith.clj.core.path-finder.interface.status :as status]
            [polylith.clj.core.text-table.interface :as text-table]
            [polylith.clj.core.util.interface.color :as color]))

(defn profile-cell [index project-name column is-show-resources path-entries]
  (let [flags (status/project-status-flags path-entries project-name is-show-resources)]
    (text-table/cell column (+ index 3) flags :purple :center)))

(defn profile-col [index {:keys [name] :as profile} disk-paths projects is-show-resources]
  (let [column (+ 11 (* 2 index))
        path-entries (extract/from-profiles-paths disk-paths profile)]
    (concat [(text-table/cell column 1 name :purple :center :horizontal)]
            (map-indexed #(profile-cell %1 %2 column is-show-resources path-entries)
                         (map :name projects)))))
(defn profile-columns [disk-paths projects profiles is-show-resources]
  (apply concat
         (map-indexed #(profile-col %1 %2 disk-paths projects is-show-resources)
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
        status-flags (str (status/project-status-flags path-entries name is-show-resources)
                          (if (contains? (set projects-to-test) name) "x" "-"))]
    (text-table/cell 5 (+ index 3) status-flags :purple :center)))

(defn status-column
  "The third '--x' column is marked if the project is marked to be tested from *any* project."
  [projects disk-paths is-show-resources]
  (concat [(text-table/cell 5 "status")]
          (map-indexed #(status-cell %1 %2 disk-paths is-show-resources)
                       projects)))

(defn dialect-cell [start-column index {:keys [source-types base-names component-names]} brick-name->source-types]
  (let [used-bricks (set (concat (:src base-names)
                                 (:test base-names)
                                 (:src component-names)
                                 (:test component-names)))
        src-types (set (concat source-types
                               (mapcat brick-name->source-types used-bricks)))
        flags (dialect-column/source-flags src-types)]
    (text-table/cell start-column (+ index 3) flags :none :center)))

(defn dialect-column [is-show-dialect projects components bases]
  (when is-show-dialect
    (let [brick-name->source-types (into {} (map (juxt :name :source-types)
                                               (concat components bases)))]
      (concat [(text-table/cell 7 "dialect")]
              (map-indexed #(dialect-cell 7 %1 %2 brick-name->source-types)
                           projects)))))

(defn dev-cell [index {:keys [name]} {:keys [projects-to-test]} path-entries is-show-resources]
  (let [status-flags (str (status/project-status-flags path-entries name is-show-resources)
                          (if (contains? (set projects-to-test) name) "x" "-"))]
    (text-table/cell 9 (+ index 3) status-flags :purple :center)))

(defn dev-column [projects is-show-resources]
  (let [development (common/find-project "development" projects)
        alias (get development :alias "dev")
        path-entries (extract/from-paths (:paths development) nil)]
    (concat [(text-table/cell 9 1 alias :purple)]
            (map-indexed #(dev-cell %1 %2 development path-entries is-show-resources)
                         projects))))

(defn loc-cell [index lines-of-code column thousand-separator]
  (text-table/number-cell column (+ index 3) lines-of-code :right thousand-separator))

(defn loc-columns [is-show-loc projects thousand-separator total-col-src total-loc-test]
  (when is-show-loc
    ;; The text table uses the column to sort the cells, and 901 + 903 are used to ensure that they are placed far enough to the right.
    (concat [(text-table/cell 901 1 "loc" :none :right)]
            (map-indexed #(loc-cell %1 %2 901 thousand-separator) (map #(-> % :lines-of-code :src) projects))
            [(text-table/number-cell 901 (+ (count projects) 3) total-col-src :right thousand-separator)]
            [(text-table/cell 903 1 "(t)" :none :right)]
            (map-indexed #(loc-cell %1 %2 903 thousand-separator) (map #(-> % :lines-of-code :test) projects))
            [(text-table/number-cell 903 (+ (count projects) 3) total-loc-test :right thousand-separator)])))

(defn table
  "These are the indexes for the different columns:

     index:      1                     3      5        7       9     11      901 903
     spaces:                2             4       6              10            902
     sections:                                              8             900

               project               alias  status  dialect   dev  profile   loc (t)
               --------------------------------------------   ------------   -------
               realworld-backend *   be      ---      jc-     ---    --       1   0
               realworld-frontend *  fe      ---      -cs     ---    --       6   6
               development *         dev     s--      jcs     s--    --       3   6"
  [{:keys [settings projects components bases changes profiles paths]} is-show-dialect is-show-loc is-show-resources]
  (let [{:keys [color-mode thousand-separator]} settings
        n#dev (count (filter :is-dev projects))
        inactive-profiles (if (zero? n#dev) [] (profile/inactive-profiles settings profiles))
        n#profiles (count inactive-profiles)
        total-loc-src (apply + (filter identity (map #(-> % :lines-of-code :src) projects)))
        total-loc-test (apply + (filter identity (map #(-> % :lines-of-code :test) projects)))
        project-col (project-column projects changes "project" :name 1 color-mode)
        alias-col (project-column projects {} "alias" :alias 3 color-mode)
        status-col (status-column projects paths is-show-resources)
        dialect-col (dialect-column is-show-dialect projects components bases)
        dev-col (if (zero? n#dev) [] (dev-column projects is-show-resources))
        profile-cols (if (zero? n#dev) [] (profile-columns paths projects inactive-profiles is-show-resources))
        loc-col (loc-columns is-show-loc projects thousand-separator total-loc-src total-loc-test)
        header-spaces (concat [2 4] ;; project<space>alias<space>status
                              (when is-show-dialect [6]) ;; status<space>dialect
                              (when (and (> n#dev 0) (> n#profiles 0)) (range 10 (+ 10 (* 2 n#profiles)) 2)) ;; dev<space>profile(s)
                              (when is-show-loc [902])) ;; loc<space>(t)
        header-space-cells (text-table/spaces 1 header-spaces (repeat "  "))
        cells (text-table/merge-cells project-col alias-col status-col dialect-col dev-col loc-col profile-cols header-space-cells)
        header-line (text-table/line 2 cells)
        header-line-spaces (concat (when (> n#dev 0) [8])
                                   (when is-show-loc [900]))
        header-line-space-cells (text-table/spaces 2 header-line-spaces (repeat "   "))]
    (text-table/table "  " color-mode cells header-line header-line-space-cells)))

(defn print-table [workspace is-show-dialect is-show-loc is-show-resources]
  (text-table/print-table (table workspace is-show-dialect is-show-loc is-show-resources)))

(comment
  (require '[dev.dev-common :as dev]
           '[polylith.clj.core.workspace.interface :as ws])
  (def workspace (ws/workspace (dev/dir ".")))
  (def workspace (ws/workspace (dev/dir "examples/profiles")))
  (def workspace (ws/workspace (dev/dir "." "skip:dev")))

  (print-table workspace true false false)
  (print-table workspace true true false)

  (print-table workspace false false false)
  (print-table workspace false true false)
  (print-table workspace true false false)
  (print-table workspace true true false)
  (print-table workspace true true true)
  #__)

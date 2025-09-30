(ns ^:no-doc polylith.clj.core.info.table.brick
  (:require [polylith.clj.core.text-table.interface :as text-table]
            [polylith.clj.core.info.table.profile :as profile]
            [polylith.clj.core.info.table.brick.ifc-column :as ifc-column]
            [polylith.clj.core.info.table.brick.brick-column :as brick-column]
            [polylith.clj.core.info.table.brick.dialect-column :as dialect-column]
            [polylith.clj.core.info.table.brick.loc-columns :as loc-columns]
            [polylith.clj.core.info.table.brick.profile-columns :as profile-columns]
            [polylith.clj.core.info.table.brick.project-columns :as proj-columns]))

(defn component-sorter [{:keys [interface name]}]
  [(:name interface) name])

(defn table
  " These are the indexes for the different columns:

    index:      1          3             5        7    9    501    503     901 903
    spaces:           2           4                  8         502           902
    sections:                                  6         500            900

            interface    brick         dialect    s1  s2    dev  profile   loc (t)
            ----------------------------------   --------   ------------   -------
            calculator   calculator1     j--     s--  s--   s--    --       1   0
            database     database1       j--     st-  st-   st-    --       6   6
            util         util1           j--     st-  st-   st-    --       1   6
            -            base1           j--     st-  st-   st-    --       1   7"
  [{:keys [settings profiles projects components bases paths changes]} is-show-dialect is-show-loc is-show-resources]
  (let [{:keys [color-mode thousand-separator]} settings
        n#dev (count (filter :is-dev projects))
        inactive-profiles (if (zero? n#dev) [] (profile/inactive-profiles settings profiles))
        sorted-components (sort-by component-sorter components)
        bricks (concat sorted-components bases)
        ifc-column (ifc-column/column sorted-components bases)
        brick-column (brick-column/column bricks changes color-mode)
        dialect-column (dialect-column/column is-show-dialect bricks)
        project-columns (proj-columns/columns projects bricks paths is-show-loc is-show-resources thousand-separator)
        profile-columns (profile-columns/columns bricks inactive-profiles paths is-show-resources)
        loc-columns (loc-columns/columns is-show-loc bricks thousand-separator)
        n#profiles (count inactive-profiles)
        n#projects (count projects)
        n#deployable-projects (- n#projects n#dev)
        header-spaces (concat [2] ;; interface<space>brick
                              (when is-show-dialect [4]) ;; brick<space>dialect
                              (when (> n#deployable-projects 0) (range 8 (+ 8 (* 2 (dec n#deployable-projects))) 2)) ;; s1<space>s2...
                              (when (and (> n#dev 0) (> n#profiles 0)) [502]) ;; dev<space>profile...
                              (when (> n#profiles 0) (range 504 (+ 504 (* 2 (dec n#profiles))) 2))) ;; profile1<space>profile2...
        header-space-cells (text-table/spaces 1 header-spaces (repeat "  "))
        header-loc-spaces (when is-show-loc [902])  ;; loc<space>(t)
        loc-space-cell (text-table/spaces 1 header-loc-spaces [" "])
        cells (text-table/merge-cells ifc-column brick-column dialect-column project-columns profile-columns loc-columns header-space-cells loc-space-cell)
        header-line (text-table/line 2 cells)
        header-line-spaces (concat (when (> n#deployable-projects 0) [6])
                                   (when (> (+ n#dev n#profiles) 0) [500])
                                   (when is-show-loc [900]))
        header-line-space-cells (text-table/spaces 2 header-line-spaces (repeat "   "))]
    (text-table/table "  " color-mode cells header-line header-line-space-cells)))

(defn print-table [workspace is-show-dialect is-show-loc is-show-resources]
  (text-table/print-table (table workspace is-show-dialect is-show-loc is-show-resources)))

(comment
  (require '[dev.dev-common :as dev]
           '[polylith.clj.core.workspace.interface :as ws])
  (def workspace (ws/workspace (dev/dir ".")))
  (def workspace (ws/workspace (dev/dir "." "color-mode:none")))
  (def workspace (ws/workspace (dev/dir "examples/profiles")))
  (def workspace (ws/workspace (dev/dir "examples/profiles" "color-mode:none")))
  (def workspace (ws/workspace (dev/dir "examples/local-dep" "color-mode:none")))
  (def workspace (ws/workspace (dev/dir "examples/local-dep" "skip:dev:inv" "color-mode:none")))
  (def workspace (ws/workspace (dev/dir "." "skip:dev")))

  (table workspace true false false)

  (print-table workspace false false false)
  (print-table workspace false true false)
  (print-table workspace true false false)
  (print-table workspace true true false)
  #__)

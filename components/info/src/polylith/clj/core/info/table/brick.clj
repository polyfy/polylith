(ns ^:no-doc polylith.clj.core.info.table.brick
  (:require [polylith.clj.core.text-table.interface :as text-table]
            [polylith.clj.core.info.table.profile :as profile]
            [polylith.clj.core.info.table.ws-column.ws-brick :as ws-brick]
            [polylith.clj.core.info.table.ws-column.ifc-column :as ifc-column]
            [polylith.clj.core.info.table.ws-column.brick-column :as brick-column]
            [polylith.clj.core.info.table.ws-column.loc-columns :as loc-columns]
            [polylith.clj.core.info.table.ws-column.profile-columns :as profile-columns]
            [polylith.clj.core.info.table.ws-column.project-columns :as proj-columns]))

(defn component-sorter [{:keys [interface name]}]
  [(:name interface) name])

(defn component-info [{:keys [name type interface]} changed-bricks]
  {:name name
   :type type
   :interface (:name interface "-")
   :changed? (contains? changed-bricks name)})

(defn base-info [{:keys [name type]} changed-bricks]
  {:name name
   :type type
   :interface "-"
   :changed? (contains? changed-bricks name)})

(defn full-name [alias name]
  (str alias "/" name))

(defn ws-component-info [{:keys [alias name type interface]} alias->workspace]
  (let [changed-components (set (-> alias alias->workspace :changes :changed-components))]
    {:name (full-name alias name)
     :type type
     :interface (full-name alias (or interface "-"))
     :changed? (contains? changed-components name)}))

(defn ws-base-info [{:keys [alias name type interface]} alias->workspace]
  (let [changed-bases (set (-> alias alias->workspace :changes :changed-bases))]
    {:name (full-name alias name)
     :type type
     :interface (full-name alias (or interface "-"))
     :changed? (contains? changed-bases name)}))

(defn table [{:keys [settings profiles projects components bases paths changes workspaces]} is-show-loc is-show-resources]
  (let [{:keys [color-mode thousand-separator]} settings
        {:keys [changed-components changed-bases]} changes
        changed-bricks (set (concat changed-components changed-bases))
        n#dev (count (filter :is-dev projects))
        alias->workspace (into {} (map (juxt :alias identity) workspaces))
        [ws-bases ws-components] (ws-brick/project-bricks projects)
        inactive-profiles (if (zero? n#dev) [] (profile/inactive-profiles settings profiles))
        sorted-components (sort-by component-sorter components)
        bricks (concat sorted-components bases)
        bricks-info (concat (map #(component-info % changed-bricks)
                                 (sort-by component-sorter components))
                            (map #(ws-component-info % alias->workspace) ws-components)
                            (map #(base-info % changed-bricks) bases)
                            (map #(ws-base-info % alias->workspace) ws-bases))
        space-columns (range 2 (* 2 (+ 2 (count projects) (count inactive-profiles) (if is-show-loc 2 0))) 2)
        spaces (concat (repeat (-> space-columns count dec) "  ") (if is-show-loc [" "] ["  "]))
        profile-start-column (+ 5 (* 2 (count projects)))
        loc-start-column (+ profile-start-column (* 2 (count inactive-profiles)))
        ifc-column (ifc-column/column bricks-info)
        brick-column (brick-column/column bricks-info color-mode)
        project-columns (proj-columns/columns projects sorted-components bases paths ws-components ws-bases alias->workspace is-show-loc is-show-resources thousand-separator)
        profile-columns (profile-columns/columns profile-start-column sorted-components bases inactive-profiles ws-components ws-bases alias->workspace paths is-show-resources)
        loc-columns (loc-columns/columns is-show-loc bricks loc-start-column thousand-separator)
        header-spaces (text-table/spaces 1 space-columns spaces)
        cells (text-table/merge-cells ifc-column brick-column project-columns profile-columns loc-columns header-spaces)
        line (text-table/line 2 cells)
        section1 (if (or (zero? n#dev)
                         (= 1 (count projects)))
                   [] [(* 2 (+ (-> projects count) n#dev))])
        section2 (if is-show-loc [(- (last space-columns) 2)] [])
        line-space (text-table/spaces 2 (concat [4] section1 section2) (repeat "   "))]
    (text-table/table "  " color-mode cells line line-space)))

(defn print-table [workspace is-show-loc is-show-resources]
  (text-table/print-table (table workspace is-show-loc is-show-resources)))

(comment
  (print-table dev.jocke/workspace false false)
  (print-table dev.jocke/workspace true false)
  #__)

(comment
  (def workspace dev.jocke/workspace)
  (def workspaces (:workspaces workspace))
  (def projects (:projects workspace))
  (def components (:components workspace))
  (def bases1 (:bases workspace))
  (def changes (:changes workspace))
  (def changed-components (:changed-components changes))
  (def changed-bases (:changed-bases changes))
  (def changed-bricks (set (concat changed-components changed-bases)))
  (def alias->workspace (into {} (map (juxt :alias identity) workspaces)))

  (let [[ws-bases ws-components] (ws-brick/ws-bricks projects)]
    (def ws-bases ws-bases)
    (def ws-components ws-components))

  (def bricks (concat (map #(component-info % changed-bricks)
                           (sort-by component-sorter components))
                      (map #(ws-component-info % alias->workspace) ws-components)
                      (map #(base-info % changed-bricks) bases1)
                      (map #(ws-base-info % alias->workspace) ws-bases)))

  (def ws-components-info (map ws-brick-info ws-components))


  (map :name sorted-components)
  (map (juxt :name :type) sorted-components)

  (print-table workspace false false)

  (def ws-bricks (ws-brick/project-bricks projects))
  #__)

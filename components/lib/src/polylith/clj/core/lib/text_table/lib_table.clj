(ns polylith.clj.core.lib.text-table.lib-table
  (:require [polylith.clj.core.util.interface.str :as str-util]
            [polylith.clj.core.text-table.interface :as text-table]))

(def type->color {"component" :green
                  "base" :blue})

(defn lib [[name {:keys [version size type]}]]
  [(cond-> {:name name}
           version (assoc :version version)
           size (assoc :size size)
           type (assoc :type type))])

(defn brick-libs [{:keys [name lib-deps]}]
  [name (set (mapcat lib lib-deps))])

(defn lib-cell [column row library]
  (text-table/cell column row library :none :left :horizontal))

(defn lib-column [libraries]
  (concat [(text-table/cell 1 1 "library" :none :left :horizontal)]
          (map-indexed #(lib-cell 1 (+ 3 %1) %2)
                       (map :name libraries))))

(defn version-column [libraries]
  (concat [(text-table/cell 3 1 "version" :none :left :horizontal)]
          (map-indexed #(lib-cell 3 (+ 3 %1) %2)
                       (map :version libraries))))

(defn size-kb [{:keys [size]} thousand-sep]
  (if size
    (str-util/sep-1000 (quot size 1024) thousand-sep)
    ""))

(defn kb-cell [row library thousand-sep]
  (let [size (size-kb library thousand-sep)]
    (text-table/cell 5 row size :none :right :horizontal)))

(defn size-column [libraries thousand-sep]
  (concat [(text-table/cell 5 1 "KB" :none :right :horizontal)]
          (map-indexed #(kb-cell (+ 3 %1) %2 thousand-sep)
                       libraries)))

(defn flag-cell [column row lib-dep lib-deps]
  (let [flag (if (contains? lib-deps lib-dep) "x" "-")]
    (text-table/cell column row flag :purple :center :horizontal)))

(defn project-column [column {:keys [alias lib-deps unmerged]} libraries]
  (let [deps (set (mapcat lib (:lib-deps unmerged lib-deps)))]
    (concat [(text-table/cell column 1 alias :purple :center :horizontal)]
            (map-indexed #(flag-cell column (+ 3 %1) %2 deps)
                         libraries))))

(defn project-columns [libraries projects]
  (apply concat (map-indexed #(project-column (+ 7 (* 2 %1)) %2 libraries)
                             projects)))

(defn profile-column [column libraries [profile {:keys [lib-deps]}]]
  (concat [(text-table/cell column 1 profile :purple :center :horizontal)]
          (map-indexed #(flag-cell column (+ 3 %1) %2 (set (mapcat lib lib-deps)))
                       libraries)))

(defn profile-columns [column libraries profile-to-settings]
  (apply concat (map-indexed #(profile-column (+ column (* 2 %1)) libraries %2)
                             profile-to-settings)))

(defn contains-lib? [library libraries]
  (or (contains? libraries library)
      (contains? libraries {:name (:name library)})))

(defn brick-cell [column row library brick-libs empty-character]
  (let [flag (if (contains-lib? library brick-libs) "x" empty-character)]
    (text-table/cell column row flag :none :left :vertical)))

(defn brick-column [column {:keys [name type]} libraries brick->libs empty-character]
  (concat [(text-table/cell column 1 name (type->color type) :right :vertical)]
          (map-indexed #(brick-cell column (+ 3 %1) %2 (brick->libs name) empty-character)
                       libraries)))

(defn brick-columns [column bricks libraries brick->libs empty-character]
  (apply concat (map-indexed #(brick-column (+ column (* 2 %1)) %2 libraries brick->libs empty-character)
                             bricks)))

(defn profile-lib [[_ {:keys [lib-deps]}]]
  (mapcat lib lib-deps))

(defn table [{:keys [settings components bases projects]} is-all]
  (let [{:keys [profile-to-settings empty-character thousand-sep color-mode compact-views]} settings
        entities (concat components bases projects)
        libraries (sort-by (juxt :name :version)
                           (set (concat (mapcat lib (mapcat :lib-deps entities))
                                        (mapcat lib (mapcat :lib-deps-test entities))
                                        (mapcat profile-lib profile-to-settings))))
        all-bricks (concat components bases)
        brick->libs (into {} (map brick-libs all-bricks))
        bricks (if is-all
                 all-bricks
                 (filter #(-> % :name brick->libs empty? not) all-bricks))
        lib-col (lib-column libraries)
        version-col (version-column libraries)
        size-col (size-column libraries thousand-sep)
        project-cols (project-columns libraries projects)
        profile-col (+ 7 (* 2 (count projects)))
        profile-cols (profile-columns profile-col libraries profile-to-settings)
        brick-col (+ profile-col (* 2 (count profile-to-settings)))
        n#projects (count projects)
        n#profiles (count profile-to-settings)
        n#bricks (count bricks)
        brick-cols (brick-columns brick-col bricks libraries brick->libs empty-character)
        space-columns (range 2 (* 2 (+ 3 n#projects n#profiles n#bricks)) 2)
        space (if (contains? compact-views "libs") " " "  ")
        spaces (text-table/spaces 1 space-columns (repeat space))
        cells (text-table/merge-cells lib-col version-col size-col project-cols profile-cols brick-cols spaces)
        line (text-table/line 2 cells)
        section2 (+ 4 (* 2 n#projects))
        section3 (+ 6 (* 2 (+ n#projects n#profiles)))
        sections (text-table/spaces 2 [6 section2 section3] (repeat "   "))]
    (text-table/table "  " color-mode cells line sections)))

(defn print-table [workspace is-all]
  (text-table/print-table (table workspace is-all)))

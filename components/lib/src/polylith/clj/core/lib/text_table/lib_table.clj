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
  [name (set (mapcat lib
                     (concat (:src lib-deps)
                             (:test lib-deps))))])

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

(defn type-column [libraries]
  (concat [(text-table/cell 5 1 "type" :none :left :horizontal)]
          (map-indexed #(lib-cell 5 (+ 3 %1) %2)
                       (map :type libraries))))

(defn size-kb [{:keys [size]} thousand-separator]
  (if size
    (str-util/sep-1000 (quot size 1024) thousand-separator)
    ""))

(defn kb-cell [row library thousand-separator]
  (let [size (size-kb library thousand-separator)]
    (text-table/cell 7 row size :none :right :horizontal)))

(defn size-column [libraries thousand-separator]
  (concat [(text-table/cell 7 1 "KB" :none :right :horizontal)]
          (map-indexed #(kb-cell (+ 3 %1) %2 thousand-separator)
                       libraries)))

(defn flag-cell [column row lib-dep src-deps test-deps]
  (let [flag-src (if (contains? src-deps lib-dep) "x" "-")
        flag-tst (if (contains? test-deps lib-dep) "x" "-")
        flag (str flag-src flag-tst)]
    (text-table/cell column row flag :purple :center :horizontal)))

(defn project-column [column {:keys [alias lib-deps unmerged]} libraries]
  (let [deps (:lib-deps unmerged lib-deps)
        src-deps (set (mapcat lib (:src deps)))
        test-deps (set (mapcat lib (:test deps)))]
    (concat [(text-table/cell column 1 alias :purple :center :horizontal)]
            (map-indexed #(flag-cell column (+ 3 %1) %2 src-deps test-deps)
                         libraries))))

(defn project-columns [libraries projects]
  (apply concat (map-indexed #(project-column (+ 9 (* 2 %1)) %2 libraries)
                             projects)))

(defn profile-flag-cell [column row lib-dep lib-deps]
  (let [flag (if (contains? lib-deps lib-dep) "x" "-")]
    (text-table/cell column row flag :purple :center :horizontal)))

(defn profile-column [column libraries [profile {:keys [lib-deps]}]]
  (let [deps (set (mapcat lib lib-deps))]
    (concat [(text-table/cell column 1 profile :purple :center :horizontal)]
            (map-indexed #(profile-flag-cell column (+ 3 %1) %2 deps)
                         libraries))))

(defn profile-columns [column libraries profile-to-settings]
  (apply concat (map-indexed #(profile-column (+ column (* 2 %1)) libraries %2)
                             profile-to-settings)))

(defn contains-lib? [library libraries]
  (or (contains? libraries library)
      (contains? libraries {:name (:name library)})))

(defn brick-cell-sign [library src-libs]
  (if (contains? src-libs library) "x" "t"))

(defn brick-cell [column row library src-libs brick-libs empty-character]
  (let [flag (if (contains-lib? library brick-libs)
               (brick-cell-sign library src-libs)
               empty-character)]
    (text-table/cell column row flag :none :left :vertical)))

(defn brick-column [column {:keys [name type]} libraries src-libs brick->libs empty-character]
  (concat [(text-table/cell column 1 name (type->color type) :right :vertical)]
          (map-indexed #(brick-cell column (+ 3 %1) %2 src-libs (brick->libs name) empty-character)
                       libraries)))

(defn brick-columns [column bricks libraries src-libs brick->libs empty-character]
  (apply concat (map-indexed #(brick-column (+ column (* 2 %1)) %2 libraries src-libs brick->libs empty-character)
                             bricks)))

(defn profile-lib [[_ {:keys [lib-deps]}]]
  (mapcat lib lib-deps))

(defn table [{:keys [settings components bases projects]} is-all]
  (let [{:keys [profile-to-settings empty-character thousand-separator color-mode compact-views]} settings
        entities (concat components bases projects)
        src-libs (set (concat (mapcat lib (mapcat #(-> % :lib-deps :src) entities))
                              (mapcat profile-lib profile-to-settings)))
        test-libs (set (mapcat lib (mapcat #(-> % :lib-deps :test) entities)))
        libraries (sort-by (juxt :name :version)
                           (set (filter :version (concat src-libs test-libs))))
        all-bricks (concat components bases)
        brick->libs (into {} (map brick-libs all-bricks))
        bricks (if is-all
                 all-bricks
                 (filter #(-> % :name brick->libs empty? not) all-bricks))
        lib-col (lib-column libraries)
        version-col (version-column libraries)
        type-col (type-column libraries)
        size-col (size-column libraries thousand-separator)
        project-cols (project-columns libraries projects)
        profile-col (+ 9 (* 2 (count projects)))
        n#dev (count (filter :is-dev projects))
        profile-cols (if (zero? n#dev) [] (profile-columns profile-col libraries profile-to-settings))
        brick-col (+ profile-col (* 2 (count profile-to-settings)))
        n#projects (count projects)
        n#profiles (if (zero? n#dev) 0 (count profile-to-settings))
        n#bricks (count bricks)
        brick-cols (brick-columns brick-col bricks libraries src-libs brick->libs empty-character)
        space-columns (range 2 (* 2 (+ 4 n#projects n#profiles n#bricks)) 2)
        space (if (contains? compact-views "libs") " " "  ")
        spaces (text-table/spaces 1 space-columns (repeat space))
        cells (text-table/merge-cells lib-col version-col type-col size-col project-cols profile-cols brick-cols spaces)
        line (text-table/line 2 cells)
        section2 (+ 6 (* 2 n#projects))
        section3 (+ 8 (* 2 (+ n#projects n#profiles)))
        sections (if (zero? n#dev) [8 section3] [8 section2 section3])
        spaces (text-table/spaces 2 sections (repeat "   "))]
    (text-table/table "  " color-mode cells line spaces)))

(defn print-table [workspace is-all]
  (text-table/print-table (table workspace is-all)))

(comment
  (require '[dev.development :as dev])
  (print-table dev/workspace false)
  #__)

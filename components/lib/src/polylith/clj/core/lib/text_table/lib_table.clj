(ns ^:no-doc polylith.clj.core.lib.text-table.lib-table
  (:require [clojure.string :as str]
            [polylith.clj.core.antq.ifc :as antq]
            [polylith.clj.core.common.interface :as common]
            [polylith.clj.core.lib.libraries :as libraries]
            [polylith.clj.core.text-table.interface :as text-table]
            [polylith.clj.core.util.interface.str :as str-util]))

(def type->color {"component" :green
                  "base" :blue})

(defn brick-libs [{:keys [name lib-deps]}]
  [name (set (mapcat libraries/lib
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

(defn latest-column [libraries lib->latest-version]
  (concat [(text-table/cell 5 1 "latest" :none :left :horizontal)]
          (map-indexed #(lib-cell 5 (+ 3 %1) %2)
                       (map #(lib->latest-version (:name %) "")
                            libraries))))

(defn type-column [libraries]
  (concat [(text-table/cell 7 1 "type" :none :left :horizontal)]
          (map-indexed #(lib-cell 7 (+ 3 %1) %2)
                       (map :type libraries))))

(defn size-kb [{:keys [size]} thousand-separator hide-lib-size?]
  (if (or (nil? size)
          hide-lib-size?)
    "-"
    (str-util/sep-1000 (quot size 1024) thousand-separator)))

(defn kb-cell [row library thousand-separator hide-lib-size?]
  (let [size (size-kb library thousand-separator hide-lib-size?)]
    (text-table/cell 9 row size :none :right :horizontal)))

(defn size-column [libraries thousand-separator hide-lib-size?]
  (concat [(text-table/cell 9 1 "KB" :none :right :horizontal)]
          (map-indexed #(kb-cell (+ 3 %1) %2 thousand-separator hide-lib-size?)
                       libraries)))

(defn flag-cell [column row lib-dep src-deps test-deps]
  (let [src (if (contains? src-deps lib-dep) 1 0)
        test (if (contains? test-deps lib-dep) 2 0)
        flag (get ["-" "x" "t" "x"] (+ src test))]
    (text-table/cell column row flag :purple :center :horizontal)))

(defn project-column [column {:keys [alias lib-deps unmerged]} libraries]
  (let [deps (:lib-deps unmerged lib-deps)
        src-deps (set (mapcat libraries/lib (:src deps)))
        test-deps (set (mapcat libraries/lib (:test deps)))]
    (concat [(text-table/cell column 1 alias :purple :center :horizontal)]
            (map-indexed #(flag-cell column (+ 3 %1) %2 src-deps test-deps)
                         libraries))))

(defn project-columns [libraries projects]
  (apply concat (map-indexed #(project-column (+ 11 (* 2 %1)) %2 libraries)
                             projects)))

(defn profile-flag-cell [column row lib-dep lib-deps]
  (let [flag (if (contains? lib-deps lib-dep) "x" "-")]
    (text-table/cell column row flag :purple :center :horizontal)))

(defn profile-column [column libraries {:keys [name lib-deps]}]
  (let [deps (set (mapcat libraries/lib lib-deps))]
    (concat [(text-table/cell column 1 name :purple :center :horizontal)]
            (map-indexed #(profile-flag-cell column (+ 3 %1) %2 deps)
                         libraries))))

(defn profile-columns [column libraries profiles]
  (apply concat (map-indexed #(profile-column (+ column (* 2 %1)) libraries %2)
                             profiles)))

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

(defn matches? [filtered-libs {:keys [name]}]
  (some #(str/starts-with? name %)
        filtered-libs))

(defn libs
  "Different use of the libs command:
     libs                           - show all libraries
     libs :outdated libraries:lib   - show all libraries (and the 'latest' column)
     libs :update                   - only update, don't show table of libraries
     libs libraries:lib1            - filter out 'lib1' and only show that
     libs libraries:lib1:lib2       - only show these two libraries"
  [workspace is-outdated filtered-libs]
  (let [{:keys [libraries src-libs]} (libraries/libs workspace)
        libraries (if (and filtered-libs (not is-outdated))
                    (filterv #(matches? filtered-libs %)
                             libraries)
                    libraries)]
    {:src-libs src-libs
     :libraries libraries}))

(defn table [{:keys [configs settings user-input profiles components bases projects] :as workspace}]
  (let [{:keys [empty-character thousand-separator color-mode]} settings
        {:keys [is-outdated is-hide-lib-size] filtered-libs :libraries} user-input
        calculate-latest-version? (common/calculate-latest-version? user-input)
        lib->latest-version (antq/library->latest-version configs calculate-latest-version?)
        {:keys [libraries src-libs]} (libs workspace is-outdated filtered-libs)
        all-bricks (concat components bases)
        brick->libs (into {} (map brick-libs all-bricks))
        bricks (filter #(-> % :name brick->libs empty? not) all-bricks)
        lib-col (lib-column libraries)
        version-col (version-column libraries)
        latest-col (when is-outdated (latest-column libraries lib->latest-version))
        type-col (type-column libraries)
        size-col (size-column libraries thousand-separator is-hide-lib-size)
        project-cols (project-columns libraries projects)
        n#dev (count (filter :is-dev projects))
        n#projects (- (count projects) n#dev)
        profile-col (+ 11 (* 2 (+ n#dev n#projects)))
        profile-cols (if (zero? n#dev) [] (profile-columns profile-col libraries profiles))
        n#profiles (if (zero? n#dev) 0 (count profiles))
        brick-col (+ profile-col (* 2 n#profiles))
        n#bricks (count bricks)
        brick-cols (brick-columns brick-col bricks libraries src-libs brick->libs empty-character)
        end-space-column (* 2 (+ 5 n#projects n#dev n#profiles))
        space-brick-columns (range (+ 2 end-space-column) (+ end-space-column (* 2 n#bricks)) 2)
        compact? (common/compact? workspace "libs")
        space (if compact? " " "  ")
        spaces (concat (text-table/spaces 1 (range 2 6 2) (repeat "  "))
                       (text-table/spaces 1 (range (if is-outdated 6 8) end-space-column 2) (repeat "  "))
                       (text-table/spaces 1 space-brick-columns (repeat space)))
        cells (text-table/merge-cells lib-col version-col latest-col type-col size-col project-cols profile-cols brick-cols spaces)
        line (text-table/line 2 cells)
        brick-spacing (if (zero? n#bricks) -1 0)
        section1 10
        section2 (+ 10 (* 2 n#projects))
        section3 (+ 10 (* 2 (+ n#projects n#dev n#profiles brick-spacing)))
        spaces (text-table/spaces 2 [section1 section2 section3] (repeat "   "))]
    (text-table/table "  " color-mode cells line spaces)))

(defn print-table [workspace]
  (common/print-or-save-table workspace
                              #(table %)))

(comment
  (require '[dev.jocke :as dev])
  (print-table dev/workspace)
  (print-table (assoc-in dev/workspace [:user-input :is-all] true))
  (print-table (assoc-in dev/workspace [:user-input :libraries] ["c"]))
  (print-table (assoc-in dev/workspace [:user-input :is-hide-lib-size] true))
  (print-table (assoc-in dev/workspace [:user-input :is-outdated] true))
  #__)

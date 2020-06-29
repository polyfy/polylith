(ns polylith.workspace-kotlin.build-config-from-disk
  (:require [polylith.workspace-kotlin.indexedrows :as irows]
            [clojure.string :as str]))

(defn error [message index row filename]
  (str message " " (inc index) "in '" filename "': " row))

(defn invalid-row [index row filename]
  (error "invalid row on line " index row filename))

(defn cant-find-section [section filename]
  [(str "Could not find section '" section "' in '" filename "'")])

(defn quoted-string [[index row] filename]
  (let [start-index (str/index-of row "\"")]
    (if (< start-index 0)
      (invalid-row index row filename)
      (let [end-index (str/index-of row "\"" (inc start-index))]
        (if (< end-index 0)
          (invalid-row index row filename)
          (subs row (inc start-index) end-index))))))

(defn src-dirs [indexed-rows section-start src-start filename]
  (let [[main-index _] (irows/find-first-that-starts-with indexed-rows section-start)]
    main-index
    (if (< main-index 0)
      (cant-find-section section-start filename)
      (let [[start-index _] (irows/find-first-that-starts-with indexed-rows main-index src-start)]
        (if (< start-index 0)
          (cant-find-section section-start filename)
          (let [[end-index _] (irows/find-first-that-contains indexed-rows start-index ")")]
            (if (< end-index 0)
              [(str "Could not find ending ')' for section '" src-start "' in '" filename "'")]
              (mapv #(quoted-string % filename)
                    (irows/filter-matched indexed-rows start-index (inc end-index) "\"")))))))))

(defn main-sources [indexed-rows config-file]
  (src-dirs indexed-rows "sourceSets.main {" "kotlin.srcDirs(" config-file))

(defn test-sources [indexed-rows config-file]
  (src-dirs indexed-rows "sourceSets.test {" "kotlin.srcDirs(" config-file))

(defn sources [ws-path]
  (let [config-file "build.gradle.kts"
        config-path (str ws-path "/" config-file)
        rows (str/split-lines (slurp config-path))
        indexed-rows (vec (map-indexed vector rows))]
    {:main-sources (main-sources indexed-rows config-file)
     :test-sources (test-sources indexed-rows config-file)}))

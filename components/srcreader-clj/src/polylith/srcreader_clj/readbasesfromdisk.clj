(ns polylith.srcreader-clj.readbasesfromdisk
  (:require [polylith.srcreader-clj.readimportsfromdisk :as importsfromdisk]
            [polylith.file.interface :as file]))

(defn read-base-from-disk [ws-path top-src-dir base-name]
  (let [bases-src-dir (str ws-path "/bases/" base-name "/src/" top-src-dir)
        imports (importsfromdisk/all-imports bases-src-dir)]
    {:type "base"
     :name base-name
     :imports imports}))
     ;:dependencies (deps/dependencies top-ns base-name component-names imports)}))

(defn read-bases-from-disk [ws-path top-src-dir]
  (let [base-names (file/directory-paths (str ws-path "/bases"))]
    (mapv #(read-base-from-disk ws-path top-src-dir %) base-names)))

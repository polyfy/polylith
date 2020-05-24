(ns polylith.common.readbasesfromdisk
  (:require [polylith.common.dependencies :as deps]
            [polylith.common.readimportsfromdisk :as importsfromdisk]
            [polylith.file.interface :as file]))

(defn read-base-from-disk [ws-path top-ns top-src-dir component-names base-name]
  (let [bases-src-dir (str ws-path "/bases/" base-name "/src/" top-src-dir)
        imports (importsfromdisk/all-imports bases-src-dir)
        {:keys [deps illegal-deps]} (deps/dependencies top-ns base-name component-names imports)]
    {:type "base"
     :name base-name
     :imports imports
     :dependencies deps
     :illegal-deps illegal-deps}))

(defn read-bases-from-disk [ws-path top-ns top-src-dir component-names]
  (let [base-names (file/directory-paths (str ws-path "/bases"))]
    (mapv #(read-base-from-disk ws-path top-ns top-src-dir component-names %) base-names)))

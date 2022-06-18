(ns polylith.clj.core.workspace-clj.non-top-namespace
  (:require [clojure.string :as str]
            [polylith.clj.core.file.interface :as file]
            [polylith.clj.core.common.interface :as common]
            [polylith.clj.core.util.interface.str :as str-util]))

(defn is-not-top-ns? [path top-src-dir]
  (not (str/starts-with? path top-src-dir)))

(defn non-top-ns-map [brick-type brick-name source-dir path]
  (let [non-top-ns (if-let [rev-path (str-util/skip-until (str/reverse path) "/")]
                     (common/path-to-ns (str/reverse rev-path))
                     (common/path-to-ns path))]
    {:non-top-ns non-top-ns
     :file (str brick-type "s/" brick-name "/" source-dir "/" path)}))

(defn non-data-reader-file? [path]
  (let [filename (last (str/split path #"/"))]
    (not (contains? #{"data_readers.clj"
                      "data_readers.cljc"} filename))))

(defn non-top-namespaces-for-source [brick-type brick-name brick-dir top-src-dir source-dir]
  (let [path (str brick-dir "/" source-dir)
        path-with-slash (str path "/")]
    (->> path
         (file/visible-paths-recursively)
         (into []
               (comp
                 (filter #(and (-> % file/file file/directory? not)
                               (non-data-reader-file? (str %))))
                 (map #(str-util/skip-prefix (str %) path-with-slash))
                 (filter #(is-not-top-ns? % top-src-dir))
                 (map #(non-top-ns-map brick-type brick-name source-dir %)))))))

(defn non-top-namespaces [brick-type brick-name brick-dir top-src-dir source-paths]
  (let [namespaces (mapcat #(non-top-namespaces-for-source brick-type brick-name brick-dir top-src-dir %)
                           (filter #(not= "resources" %)
                                   source-paths))]
    (when (seq namespaces)
      (vec namespaces))))

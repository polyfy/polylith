(ns polylith.clj.core.workspace-clj.brick-paths
  (:require
   [polylith.clj.core.workspace-clj.namespaces-from-disk :as ns-from-disk]
   [polylith.clj.core.workspace-clj.interface-defs-from-disk :as defs-from-disk]
   [polylith.clj.core.file.interface :as file]))

(defn existing-paths [brick-dir paths]
  (filterv #(file/exists (str brick-dir "/" %)) paths))

(defn source-paths [brick-dir {:keys [paths aliases]}]
  {:src  (existing-paths brick-dir paths)
   :test (existing-paths brick-dir (-> aliases :test :extra-paths))})


(defn make-brick-paths [brick-paths component-dir top-src-dir]
  (vec (map #(str component-dir "/" % "/" top-src-dir) brick-paths)))


(defn make-src-dirs [component-src-dirs interface-path-name]
  (vec (map #(str % interface-path-name) component-src-dirs)))


(defn make-namespaces [src-paths test-paths]
  (let [src (vec (flatten (mapv ns-from-disk/source-namespaces-from-disk src-paths)))
              test (vec (flatten (mapv ns-from-disk/source-namespaces-from-disk test-paths)))]
          (cond-> {}
            (seq src) (assoc :src src)
            (seq test) (assoc :test test))))


(defn make-definitions [src-dirs interface-ns]
  (vec (flatten (map #(defs-from-disk/defs-from-disk % interface-ns) src-dirs))))

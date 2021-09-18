(ns polylith.clj.core.workspace-clj.non-top-namespace
  (:require [clojure.string :as str]
            [polylith.clj.core.file.interface :as file]
            [polylith.clj.core.common.interface :as common]
            [polylith.clj.core.util.interface.str :as str-util]))

(defn brick? [path]
  (or (str/starts-with? path "components")
      (str/starts-with? path "bases")))

(defn as-namespaces [brick-nss]
  (map #(str/replace % "_" "-") brick-nss))

(defn non-top-namespace [path top-nss]
  (let [parts (str/split path #"/")
        brick (first (drop 1 parts))
        n#nss (count top-nss)
        brick-nss (as-namespaces (take n#nss (drop 3 parts)))]
    (when (and (not= brick-nss top-nss)
               (not (contains? #{"data_readers.clj"
                                 "data_readers.cljc"} (last parts))))
      [{:brick brick
        :file path
        :non-top-ns (str/join "." brick-nss)}])))

(defn add-non-top-ns [result {:keys [brick non-top-ns file]}]
  (if (contains? result brick)
    (assoc result brick (conj (result brick) {:non-top-ns non-top-ns :file file}))
    (assoc result brick #{{:non-top-ns non-top-ns :file file}})))

(defn brick->non-top-namespaces [ws-dir top-namespace]
  (let [top-nss (str/split top-namespace #"\.")
        non-nss (set (mapcat #(non-top-namespace % top-nss)
                             (filterv brick?
                                      (common/filter-clojure-paths
                                        (map #(str-util/skip-prefix % (str ws-dir "/"))
                                             (file/paths-recursively ws-dir))))))]
    (reduce add-non-top-ns {} non-nss)))

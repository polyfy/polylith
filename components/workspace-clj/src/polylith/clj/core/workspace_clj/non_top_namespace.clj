(ns polylith.clj.core.workspace-clj.non-top-namespace
  (:require [clojure.string :as str]
            [polylith.clj.core.file.interface :as file]
            [polylith.clj.core.common.interface :as common]
            [polylith.clj.core.util.interface.str :as str-util]))

(def bricks->brick {"components" "component"
                    "bases" "base"})

(defn brick? [path]
  (or (str/starts-with? path "components")
      (str/starts-with? path "bases")))

(defn non-top-namespace [path top-nss]
  (let [parts (str/split path #"/")
        type (bricks->brick (first parts))
        name (first (drop 1 parts))
        n#nss (count top-nss)
        brick-nss (take n#nss (drop 3 parts))]
    (when (not= brick-nss top-nss)
      [{:name name
        :type type
        :file path
        :non-top-ns (str/join "." brick-nss)}])))

(defn non-top-namespaces [ws-dir top-namespace]
  (let [top-nss (str/split top-namespace #"\.")]
    (vec (sort-by :file
                  (set (mapcat #(non-top-namespace % top-nss)
                               (filterv brick?
                                        (common/filter-clojure-paths
                                          (map #(str-util/skip-prefix % (str ws-dir "/"))
                                               (file/paths-recursively ws-dir))))))))))

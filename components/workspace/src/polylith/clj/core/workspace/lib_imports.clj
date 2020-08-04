(ns polylith.clj.core.workspace.lib-imports
  (:require [clojure.string :as str]))

(defn library? [import suffixed-top-ns interface-names]
  (if (str/starts-with? import suffixed-top-ns)
    (let [interface-ns (subs import (count suffixed-top-ns))
          index (str/index-of interface-ns ".")
          interface (if (< index 0)
                      interface-ns
                      (subs interface-ns 0 index))]
      (not (contains? interface-names interface)))
    true))

(defn lib-imports-src [suffixed-top-ns interface-names brick]
  (vec (sort (filter #(library? % suffixed-top-ns interface-names)
                     (set (mapcat :imports (:namespaces-src brick)))))))

(defn lib-imports-test [top-ns interface-names brick]
  (vec (sort (filter #(library? % top-ns interface-names)
                     (set (mapcat :imports (:namespaces-test brick)))))))

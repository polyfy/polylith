(ns polylith.workspace.lib-imports
  (:require [clojure.string :as str]))

(defn library? [import top-ns interface-names]
  (if (str/starts-with? import top-ns)
    (let [interface-ns (subs import (count top-ns))
          index (str/index-of interface-ns ".")
          interface (if (< index 0)
                      interface-ns
                      (subs interface-ns 0 index))]
      (not (contains? interface-names interface)))
    true))

(defn src-lib-imports [top-ns interface-names brick]
  (vec (sort (filter #(library? % top-ns interface-names)
                     (set (mapcat :imports (:src-namespaces brick)))))))

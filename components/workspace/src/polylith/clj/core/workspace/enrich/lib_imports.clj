(ns ^:no-doc polylith.clj.core.workspace.enrich.lib-imports
  (:require [clojure.string :as str]))

(defn library? [import suffixed-top-ns interface-names]
  (if (str/starts-with? import suffixed-top-ns)
    (let [interface-ns (subs import (count suffixed-top-ns))
          index (str/index-of interface-ns ".")
          interface (if (nil? index)
                      interface-ns
                      (subs interface-ns 0 index))]
      (not (contains? interface-names interface)))
    true))

(defn lib-imports-source [suffixed-top-ns interface-names brick source-key]
  (vec (sort (filter #(library? % suffixed-top-ns interface-names)
                     (set (mapcat :imports (-> brick :namespaces source-key)))))))

(defn lib-imports [suffixed-top-ns interface-names brick]
  (let [src (lib-imports-source suffixed-top-ns interface-names brick :src)
        test (lib-imports-source suffixed-top-ns interface-names brick :test)]
    (cond-> {}
            (seq src) (assoc :src src)
            (seq test) (assoc :test test))))

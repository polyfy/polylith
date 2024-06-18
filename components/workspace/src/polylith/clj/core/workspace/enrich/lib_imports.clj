(ns ^:no-doc polylith.clj.core.workspace.enrich.lib-imports
  (:require [clojure.string :as str]))

(defn library? [import suffixed-top-ns brick-names]
  (if (str/starts-with? import suffixed-top-ns)
    (let [interface-ns (subs import (count suffixed-top-ns))
          index (str/index-of interface-ns ".")
          interface (if (nil? index)
                      interface-ns
                      (subs interface-ns 0 index))]
      (not (contains? brick-names interface)))
    true))

(defn lib-imports-source [suffixed-top-ns brick-names brick source-key]
  (vec (sort (filter #(library? % suffixed-top-ns brick-names)
                     (set (mapcat :imports (-> brick :namespaces source-key)))))))

(defn lib-imports [suffixed-top-ns interface-names base-names brick]
  (let [brick-names (set (concat interface-names base-names))
        src (lib-imports-source suffixed-top-ns brick-names brick :src)
        test (lib-imports-source suffixed-top-ns brick-names brick :test)]
    (cond-> {}
            (seq src) (assoc :src src)
            (seq test) (assoc :test test))))

(ns ^:no-doc polylith.clj.core.nav-generator.pages-generator
  (:require [polylith.clj.core.util.interface.str :as str-util]
            [polylith.clj.core.config-reader.interface :as config-reader]))

(defn page-names [[page-name & items]]
  (if (= "Reference" page-name)
    []
    (if-let [file (-> items first :file)]
      [file]
      (mapv #(-> % second :file)
            (rest items)))))

(defn entry [page]
  [page {}])

(defn read-cljdoc-pages []
  (-> (config-reader/read-edn-file "doc/cljdoc.edn" "cljdoc.edn")
      :config :cljdoc.doc/tree))

(defn strip-prefix-and-suffix [filename]
  (-> filename
      (str-util/skip-prefix "doc/")
      (str-util/skip-suffix ".adoc")))

(defn navigation []
  (into (sorted-map)
        (mapv entry
              (sort (map #(-> %
                              (strip-prefix-and-suffix))
                         (mapcat page-names
                                 (read-cljdoc-pages)))))))

(defn ci-pages []
  (->> (read-cljdoc-pages)
       (filter #(= "CI" (first %)))
       first
       (drop 2)
       (map #(-> % second :file))
       (map strip-prefix-and-suffix)
       (set)))

(comment
  (navigation)
  (ci-pages)
  #__)

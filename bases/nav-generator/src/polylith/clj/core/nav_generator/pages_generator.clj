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

(defn navigation []
  (let [cljdoc-pages (-> (config-reader/read-edn-file "doc/cljdoc.edn" "cljdoc.edn")
                         :config :cljdoc.doc/tree)]
    (into (sorted-map) (mapv entry
                             (sort (map #(-> %
                                             (str-util/skip-prefix "doc/")
                                             (str-util/skip-suffix ".adoc"))
                                        (mapcat page-names cljdoc-pages)))))))

(comment
  (navigation)
  #__)

(ns ^:no-doc polylith.clj.core.nav-generator.pages-generator
  (:require [polylith.clj.core.util.interface.str :as str-util]
            [polylith.clj.core.config-reader.interface :as config-reader]))

(defn page-name [item]
  (-> item
      second
      :file
      (str-util/skip-prefix "doc/")
      (str-util/skip-suffix ".adoc")))

(defn entry [page]
  [page {}])

(defn navigation []
  (let [cljdoc-pages (-> (config-reader/read-edn-file "doc/cljdoc.edn" "cljdoc.edn")
                         :config :cljdoc.doc/tree)]
    (into (sorted-map) (mapv entry (sort (remove nil? (map page-name cljdoc-pages)))))))

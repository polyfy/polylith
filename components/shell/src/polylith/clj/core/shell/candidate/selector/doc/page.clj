(ns ^:no-doc polylith.clj.core.shell.candidate.selector.doc.page
  (:require [polylith.clj.core.shell.candidate.creators :as c]
            [polylith.clj.core.shell.candidate.selector.doc.shared :as shared]
            [polylith.clj.core.config-reader.interface :as config-reader]
            [polylith.clj.core.ws-explorer.interface :as ws-explorer]
            [polylith.clj.core.util.interface.str :as str-util]))

(defn page-name [item]
  (-> item
      second
      :file
      (str-util/skip-prefix "doc/")
      (str-util/skip-suffix ".adoc")))

(defn entry [page]
  [page {}])

(comment
  (def cljdoc-pages (-> (config-reader/read-edn-file "doc/cljdoc.edn" "cljdoc.edn")
                        :config :cljdoc.doc/tree))
  ;; pages
  (into (sorted-map) (mapv entry (sort (remove nil? (map page-name cljdoc-pages)))))
  #__)

(def pages {"base" {},
            "build" {},
            "clojure-cli-tool" {},
            "colors" {},
            "component" {},
            "configuration" {},
            "context" {},
            "continuous-integration" {},
            "dependencies" {},
            "development" {},
            "example-systems" {},
            "explore-the-workspace" {},
            "flags" {},
            "git" {},
            "git-hooks" {},
            "install" {},
            "interface" {},
            "introduction" {},
            "libraries" {},
            "migrate" {},
            "naming" {},
            "parameters" {},
            "polyx" {},
            "profile" {},
            "project" {},
            "readme" {},
            "shell" {},
            "source-code" {},
            "tagging" {},
            "tap" {},
            "test-runners" {},
            "testing" {},
            "tools-deps" {},
            "upgrade" {},
            "validations" {},
            "workspace" {}})

(defn select [_ groups _]
  (let [current (or (get-in groups [:doc "page" :args]) [])
        values (ws-explorer/extract pages current)
        result (shared/strings values (ws-explorer/extract pages (conj current "keys")))]
    (mapv #(c/fn-comma-arg % :doc "page" #'select true)
          result)))

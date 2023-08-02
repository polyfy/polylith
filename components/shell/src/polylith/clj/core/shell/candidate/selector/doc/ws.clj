(ns ^:no-doc polylith.clj.core.shell.candidate.selector.doc.ws
  (:require [clojure.string :as str]
            [polylith.clj.core.shell.candidate.creators :as c]
            [polylith.clj.core.shell.candidate.selector.doc.shared :as shared]
            [polylith.clj.core.ws-explorer.interface :as ws-explorer]))

(defn ws-key [bookmark]
  [(subs bookmark 3) []])

(comment
  ;; ws-structure
  (into (sorted-map)
        (mapv ws-key
              (filter #(str/starts-with? % "== ")
                      (-> "doc/workspace-structure.adoc"
                          slurp
                          str/split-lines))))
  #__)

(def ws-structure {"bases" [],
                   "changes" [],
                   "components" [],
                   "configs" [],
                   "interface" [],
                   "messages" [],
                   "name" [],
                   "old" [],
                   "paths" [],
                   "projects" [],
                   "settings" [],
                   "user-input" [],
                   "version" [],
                   "ws-dir" [],
                   "ws-local-dir" [],
                   "ws-reader" [],
                   "ws-type" []})

(defn select [_ groups _]
  (let [current (or (get-in groups [:doc "ws" :args]) [])
        values (ws-explorer/extract ws-structure current)
        result (shared/strings values (ws-explorer/extract ws-structure (conj current "keys")))]
    (mapv #(c/fn-comma-arg % :doc "ws" #'select true)
          result)))

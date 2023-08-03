(ns ^:no-doc polylith.clj.core.shell.candidate.selector.doc.page
  (:require [polylith.clj.core.shell.candidate.creators :as c]
            [polylith.clj.core.doc.interface :as doc]
            [polylith.clj.core.shell.candidate.selector.doc.shared :as shared]
            [polylith.clj.core.ws-explorer.interface :as ws-explorer]))

(defn select [_ groups _]
  (let [current (or (get-in groups [:doc "page" :args]) [])
        values (ws-explorer/extract doc/pages current)
        result (shared/strings values (ws-explorer/extract doc/pages (conj current "keys")))]
    (mapv #(c/fn-comma-arg % :doc "page" #'select true)
          result)))

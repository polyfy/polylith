(ns ^:no-doc polylith.clj.core.shell.candidate.selector.doc.ws
  (:require [polylith.clj.core.doc.interface :as doc]
            [polylith.clj.core.shell.candidate.creators :as c]
            [polylith.clj.core.shell.candidate.selector.doc.shared :as shared]
            [polylith.clj.core.ws-explorer.interface :as ws-explorer]))

(defn select [_ groups _]
  (let [current (or (get-in groups [:doc "ws" :args]) [])
        values (ws-explorer/extract doc/ws-nav current)
        result (shared/strings values (ws-explorer/extract doc/ws-nav (conj current "keys")))]
    (mapv #(c/fn-comma-arg % :doc "ws" #'select true)
          result)))

(ns ^:no-doc polylith.clj.core.shell.candidate.selector.doc.command
  (:require [polylith.clj.core.doc.interface :as doc]
            [polylith.clj.core.shell.candidate.creators :as c]
            [polylith.clj.core.shell.candidate.selector.doc.shared :as shared]
            [polylith.clj.core.ws-explorer.interface :as ws-explorer]))

(defn select [_ groups _]
  (let [current (or (get-in groups [:doc "command" :args]) [])
        values (ws-explorer/extract doc/command-nav current)
        result (shared/strings values (ws-explorer/extract doc/command-nav (conj current "keys")))]
    (mapv #(c/fn-comma-arg % :doc "command" #'select true)
          result)))

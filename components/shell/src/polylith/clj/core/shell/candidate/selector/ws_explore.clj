(ns polylith.clj.core.shell.candidate.selector.ws-explore
  (:require [polylith.clj.core.ws-explorer.interface :as ws-explorer]
            [polylith.clj.core.shell.candidate.creators :as c]
            [polylith.clj.core.shell.candidate.selector.ws-explore :as ws-explore]))

(defn map-strings [values]
  (if (-> values first keyword?)
    (map name values)
    values))

(defn seq-strings [values raw-values]
  (if (-> raw-values first string?)
      []
      values))

(defn strings [raw-values values]
  (cond (map? raw-values) (map-strings values)
        (sequential? raw-values) (seq-strings values raw-values)
        :else values))

(defn select [_ groups workspace]
  (let [current (or (get-in groups [:ws "get" :args]) [])
        values (ws-explorer/extract workspace current)
        result (strings values (ws-explorer/extract workspace (conj current "keys")))]
    (mapv #(c/fn-comma-arg % :ws "get" #'ws-explore/select true)
          result)))

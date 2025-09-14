(ns ^:no-doc polylith.clj.core.shell.candidate.selector.dialect
  (:require [polylith.clj.core.shell.candidate.creators :as c]))

(defn select [{:keys [group]} _ {:keys [ws-dialects]}]
  (mapv #(c/fn-explorer-child-plain % true group #'select)
        ws-dialects))

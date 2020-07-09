(ns polylith.clj.core.cli.cmd.info
  [:require [clojure.pprint :as pp]
            [polylith.clj.core.workspace.interfc :as ws]])

(defn execute [workspace arg]
  (if (= "-dump" arg)
    (pp/pprint workspace)
    (ws/print-table workspace false)))

(ns polylith.core.cli.cmd.ws
  [:require [clojure.pprint :as pp]
            [polylith.core.workspace.interface :as ws]])

(defn execute [workspace arg]
  (if (= "-dump" arg)
    (pp/pprint workspace)
    (ws/print-table workspace)))

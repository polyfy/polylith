(ns polylith.cli.cmd.print-ws
  [:require [clojure.pprint :as pp]])

(defn execute [workspace]
  (pp/pprint workspace))

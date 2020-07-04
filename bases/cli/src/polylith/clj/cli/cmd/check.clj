(ns polylith.clj.cli.cmd.check
  (:require [polylith.core.util.interfc.color :as color]
            [polylith.core.common.interfc :as common]))

(defn execute [{:keys [messages settings] :as workspace}]
  (let [color-mode (:color-mode settings color/none)]
    (if (empty? messages)
      (println (color/ok color-mode "OK"))
      (println (common/pretty-messages workspace)))))

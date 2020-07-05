(ns polylith.clj.cli-tool.cli.cmd.check
  (:require [polylith.clj.core.util.interfc.color :as color]
            [polylith.clj.core.common.interfc :as common]))

(defn execute [{:keys [messages settings] :as workspace}]
  (let [color-mode (:color-mode settings color/none)]
    (if (empty? messages)
      (println (color/ok color-mode "OK"))
      (println (common/pretty-messages workspace)))))

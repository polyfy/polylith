(ns polylith.clj.core.shell.candidate.setup
  (:require [clojure.test :refer :all]
            [polylith.clj.core.shell.candidate.engine :as engine]))

(defn reset-ws []
  (let [workspace (-> "components/test-helper/resources/workspace.edn"
                    slurp read-string)]
   (reset! engine/ws workspace)
   nil))

(defn reset-ws-and-call-fn [function]
  (reset-ws)
  (function))

(defn candidates [& words]
  (->> words
       engine/select-candidate
       :candidates
       (mapv :display)
       sort
       vec))

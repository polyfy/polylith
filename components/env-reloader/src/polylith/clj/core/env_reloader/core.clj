(ns polylith.clj.core.env-reloader.core
  (:require [clojure.tools.namespace.repl :as repl]
            [polylith.clj.core.change.interfc :as change]
            [polylith.clj.core.util.interfc :as util]
            [polylith.clj.core.workspace.interfc :as ws]
            [polylith.clj.core.workspace-clj.interfc :as ws-clj]))

(defn reload [env]
  (let [{:keys [environments]} (-> "."
                                   ws-clj/workspace-from-disk
                                   ws/enrich-workspace
                                   change/with-changes)
        environment (util/find-first #(= env (:name %)) environments)
        paths (sort (set (concat (:paths environment) (:test-paths environment))))]
    (if (nil? environments)
      (println "Could not find environment '" env "'.")
      (do
        (apply repl/set-refresh-dirs paths)
        (repl/refresh-all)))))

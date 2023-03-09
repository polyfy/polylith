(ns polylith.clj.core.common.config.validate
  (:require [polylith.clj.core.file.interface :as file]
            [polylith.clj.core.util.interface.color :as color]
            [polylith.clj.core.common.config.read :as config]))

(defn valid-ws-deps1-file-found? [path color-mode]
  (try
    (and (file/exists (str path "/deps.edn"))
         (:polylith (config/read-deps-file (str path "/deps.edn"))))
    (catch Exception e
      (println (str (color/error color-mode "  Error: ") "couldn't read deps.edn: " (.getMessage e))))))

(defn valid-ws-file-found? [path color-mode]
  (try
    (and (file/exists (str path "/workspace.edn"))
         (read-string (slurp (str path "/workspace.edn"))))
    (catch Exception e
      (println (str (color/error color-mode "  Error: ") "couldn't read workspace.edn: " (.getMessage e))))))

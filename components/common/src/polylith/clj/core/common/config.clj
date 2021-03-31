(ns polylith.clj.core.common.config
  (:require [polylith.clj.core.file.interface :as file]
            [polylith.clj.core.util.interface.color :as color]
            [polylith.clj.core.common.leiningen :as leiningen]))

(defn valid-ws-lein-file-found? [path color-mode]
  (try
    (and (file/exists (str path "/project.clj"))
         (leiningen/config-key path :polylith))
    (catch Exception e
      (println (str (color/error color-mode "  Error: ") "couldn't read project.clj: " (.getMessage e))))))

(defn valid-ws-deps1-file-found? [path color-mode]
  (try
    (and (file/exists (str path "/deps.edn"))
         (:polylith (read-string (slurp (str path "/deps.edn")))))
    (catch Exception e
      (println (str (color/error color-mode "  Error: ") "couldn't read deps.edn: " (.getMessage e))))))

(defn valid-ws-file-found? [path color-mode]
  (try
    (and (file/exists (str path "/workspace.edn"))
         (read-string (slurp (str path "/workspace.edn"))))
    (catch Exception e
      (println (str (color/error color-mode "  Error: ") "couldn't read workspace.edn: " (.getMessage e))))))

(defn valid-ws-root-config-file-found? [path color-mode]
  (or (valid-ws-file-found? path color-mode)
      (valid-ws-deps1-file-found? path color-mode)
      (valid-ws-lein-file-found? path color-mode)))

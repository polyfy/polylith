(ns polylith.clj.core.common.config
  (:require [polylith.clj.core.file.interface :as file]
            [polylith.clj.core.util.interface.color :as color]))

(defn valid-deps-file? [ws-dir color-mode]
  (try
    (and (file/exists (str ws-dir "/deps.edn"))
         (read-string (slurp (str ws-dir "/deps.edn"))))
    (catch Exception e
      (println (str (color/error color-mode "  Error: ") "couldn't read deps.edn: " (.getMessage e))))))

(defn valid-ws-file? [ws-dir color-mode]
  (try
    (or (not (file/exists (str ws-dir "/workspace.edn")))
        (read-string (slurp (str ws-dir "/workspace.edn"))))
    (catch Exception e
      (println (str (color/error color-mode "  Error: ") "couldn't read workspace.edn: " (.getMessage e))))))

(defn valid-config-file? [ws-dir color-mode]
  (and (valid-deps-file? ws-dir color-mode)
       (valid-ws-file? ws-dir color-mode)))

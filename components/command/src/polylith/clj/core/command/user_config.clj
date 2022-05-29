(ns polylith.clj.core.command.user-config
  (:require [clojure.java.io :as io]
            [polylith.clj.core.file.interface :as file]
            [polylith.clj.core.user-config.interface :as user-config]
            [polylith.clj.core.util.interface.os :as os]))

(defn user-config-content []
  (let [color-mode (if (os/windows?) "none" "dark")]
    [(str "{:color-mode \"" color-mode "\"")
     (str " :empty-character \".\"")
     (str " :thousand-separator \",\"}")]))

(defn create-user-config-if-not-exists []
  (let [user-config-filename (user-config/config-file-path)]
    (when (-> user-config-filename file/exists not)
      (file/create-missing-dirs user-config-filename)
      ;; if a legacy configuration exists, copy it to the new
      ;; location and add a note at the top of the old file:
      (let [legacy-config (user-config/legacy-config-file-path)]
        (if (file/exists legacy-config)
          (let [legacy-content (slurp (io/file legacy-config))]
            (file/copy-file legacy-config user-config-filename)
            (spit (io/file legacy-config)
                  (str ";; migrated to: " legacy-config
                       "\n\n" legacy-content)))
          (file/create-file user-config-filename (user-config-content)))))))

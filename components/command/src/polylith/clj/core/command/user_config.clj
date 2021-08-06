(ns polylith.clj.core.command.user-config
  (:require [polylith.clj.core.file.interface :as file]
            [polylith.clj.core.user-config.interface :as user-config]
            [polylith.clj.core.util.interface.os :as os]))

(defn user-config-content []
  (let [color-mode (if (os/windows?) "none" "dark")]
    [(str "{:color-mode \"" color-mode "\"")
     (str " :empty-character \".\"")
     (str " :thousand-separator \",\"}")]))

(defn create-user-config-if-not-exists []
  (let [home-dir (user-config/home-dir)
        user-config-filename (str home-dir "/.polylith/config.edn")]
    (when (-> user-config-filename file/exists not)
      (file/create-missing-dirs user-config-filename)
      (file/create-file user-config-filename (user-config-content)))))

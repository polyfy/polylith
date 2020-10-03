(ns polylith.clj.core.creator.environment
  (:require [polylith.clj.core.file.interface :as file]
            [polylith.clj.core.util.interface.color :as color]
            [polylith.clj.core.common.interface :as common]
            [polylith.clj.core.git.interface :as git]))

(defn create-env [ws-dir env]
  (let [env-path (str ws-dir "/environments/" env)
        filename (str env-path "/deps.edn")]
    (file/create-dir env-path)
    (file/create-file filename
                      [(str "{:paths []")
                       ""
                       (str " :deps {org.clojure/clojure {:mvn/version \"1.10.1\"}")
                       (str "        org.clojure/tools.deps.alpha {:mvn/version \"0.8.695\"}}")
                       ""
                       (str " :aliases {:test {:extra-paths []")
                       (str "                  :extra-deps  {}}}}")])
    (git/add ws-dir filename)))

(defn print-alias-message [env color-mode]
  (let [message (str "  It's recommended to add an alias to :env-to-alias in ./deps.edn for the "
                     (color/environment env color-mode) " environment.")]
    (println message)))

(defn create [{:keys [ws-dir environments settings]} env]
  (let [color-mode (:color-mode settings color/none)]
    (if (common/find-environment env environments)
      (println (str "  Environment " (color/environment env color-mode) " (or alias) already exists."))
      (do
        (create-env ws-dir env)
        :ok))))

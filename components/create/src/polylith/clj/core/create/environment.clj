(ns polylith.clj.core.create.environment
  (:require [polylith.clj.core.file.interfc :as file]
            [polylith.clj.core.util.interfc.color :as color]
            [polylith.clj.core.common.interfc :as common]))

(defn str-attr [attr color-mode]
  (color/yellow color-mode (str "\"" attr "\"")))

(defn env-key [env alias color-mode]
  (color/purple color-mode (str (str-attr env color-mode) " " (str-attr alias color-mode))))

(defn create-env [ws-root-path env]
  (let [path (str ws-root-path "/environments/" env)]
    (file/create-dir path)
    (file/create-file (str path "/deps.edn")
                      [""
                       (str "{:paths []")
                       ""
                       (str " :deps {org.clojure/clojure {:mvn/version \"1.10.1\"}")
                       (str "        org.clojure/tools.deps.alpha {:mvn/version \"0.8.695\"}}")
                       ""
                       (str " :aliases {:test {:extra-paths []")
                       (str "                  :extra-deps  {}}}}")])))

(defn print-alias-message [env color-mode]
  (let [alias (subs env 0 1)
        message (str "Feel free to add a short name for the "
                     (color/environment env color-mode) " environment "
                     "to the :env-short-names key in deps.edn, e.g.: "
                     "{" (env-key env alias color-mode) "}")]
    (println message)))

(defn create [ws-root-path {:keys [environments settings]} env]
  (let [color-mode (:color-mode settings color/none)]
    (if (common/find-environment env environments)
      (println (str "Environment " (color/environment env color-mode) " (or alias) already exists."))
      (do
        (create-env ws-root-path env)
        :ok))))

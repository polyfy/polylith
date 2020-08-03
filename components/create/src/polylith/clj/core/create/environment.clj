(ns polylith.clj.core.create.environment
  (:require [polylith.clj.core.file.interfc :as file]
            [polylith.clj.core.util.interfc.color :as color]
            [polylith.clj.core.common.interfc :as common]))

(defn create-env [current-dir env]
  (let [env-path (str current-dir "/environments/" env)]
    (file/create-dir env-path)
    (file/create-file (str env-path "/deps.edn")
                      [""
                       (str "{:paths []")
                       ""
                       (str " :deps {org.clojure/clojure {:mvn/version \"1.10.1\"}")
                       (str "        org.clojure/tools.deps.alpha {:mvn/version \"0.8.695\"}}")
                       ""
                       (str " :aliases {:test {:extra-paths []")
                       (str "                  :extra-deps  {}}}}")])))

(defn print-alias-message [env color-mode]
  (let [message (str "It's recommended to add an alias to :env->alias in deps.edn for the "
                     (color/environment env color-mode) " environment.")]
    (println message)))

(defn create [current-dir {:keys [environments settings]} env]
  (let [color-mode (:color-mode settings color/none)]
    (if (common/find-environment env environments)
      (println (str "Environment " (color/environment env color-mode) " (or alias) already exists."))
      (do
        (create-env current-dir env)
        :ok))))

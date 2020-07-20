(ns polylith.clj.core.create.environment
  (:require [polylith.clj.core.file.interfc :as file]
            [polylith.clj.core.util.interfc :as util]
            [polylith.clj.core.util.interfc.color :as color]))

(defn str-attr [attr color-mode]
  (color/yellow color-mode (str "\"" attr "\"")))

(defn env-key [env alias color-mode]
  (color/purple color-mode (str (str-attr env color-mode) " " (str-attr alias color-mode))))

(defn create-env [env alias color-mode]
  (let [path (str (file/current-path) "/environments/" env)]
    (file/create-dir path)
    (file/create-file (str path "/deps.edn")
                      [""
                       (str "{:paths []")
                       ""
                       (str " :deps {org.clojure/clojure {:mvn/version \"1.10.1\"}")
                       (str "           org.clojure/tools.deps.alpha {:mvn/version \"0.8.695\"}}")
                       ""
                       (str " :aliases {:test {:extra-paths []")
                       (str "                  :extra-deps  {}}}}")])
    (println (str "You are recommended to manually add the {" (env-key env alias color-mode) "} "
                  "alias to the " (color/purple color-mode ":env-short-names") " key in 'deps.edn'."))))

(defn create [{:keys [environments settings]} env alias]
  (let [color-mode (:color-mode settings color/none)]
    (if (util/find-first #(= env (:name %)) environments)
      (println (str "Environment " (color/environment env color-mode) " already exists."))
      (create-env env alias color-mode))))

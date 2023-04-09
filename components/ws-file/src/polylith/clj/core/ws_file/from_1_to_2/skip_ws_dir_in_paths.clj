(ns polylith.clj.core.ws-file.from-1-to-2.skip-ws-dir-in-paths
  (:require [polylith.clj.core.util.interface.str :as str-util]))

(defn skip-ws-dir-in-file-path [{:keys [file-path] :as source-ns} ws-dir]
  (assoc source-ns :file-path (str-util/skip-prefix file-path ws-dir)))

(defn skip-in-source-paths [source-namespaces ws-dir]
  (mapv #(skip-ws-dir-in-file-path % ws-dir)
        source-namespaces))

(defn skip-in-ns-paths [{:keys [namespaces namespaces-src namespaces-test] :as entity} ws-dir]
  (let [namespaces (or namespaces {:src namespaces-src
                                   :test namespaces-test})
        {:keys [src test]} namespaces
        namespaces (cond-> namespaces
                           src (assoc :src (skip-in-source-paths src ws-dir))
                           test (assoc :test (skip-in-source-paths test ws-dir)))]
    (assoc entity :namespaces namespaces)))

(defn skip-in-entities [entities ws-dir]
  (mapv #(skip-in-ns-paths % ws-dir) entities))

(defn convert [{:keys [components bases projects] :as ws}]
  (let [ws-dir (str (:ws-dir ws) "/")]
    (assoc ws :components (skip-in-entities components ws-dir)
              :bases (skip-in-entities bases ws-dir)
              :projects (skip-in-entities projects ws-dir))))

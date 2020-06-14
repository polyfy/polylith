(ns polylith.workspace.interface
  (:require [polylith.workspace.core :as core]
            [polylith.workspace.source :as source]
            [polylith.workspace-clj.interface :as ws-clojure]))

(def workspace (ws-clojure/workspace-from-disk "../clojure-polylith-realworld-example-app"))
(def workspace (ws-clojure/workspace-from-disk "."))
(def workspace (ws-clojure/workspace-from-disk "../Nova/project-unicorn" {:polylith {:top-namespace ""}}))
(def workspace (ws-clojure/workspace-from-disk "../ws11" {:polylith {:top-namespace ""}}))
(def workspace (ws-clojure/workspace-from-disk "../ws12" {:polylith {:top-namespace ""}}))

(defn src-paths
  ([workspace env]
   (source/paths workspace env false))
  ([workspace env include-tests?]
   (source/paths workspace env include-tests?)))

(defn pimp-workspace [workspace]
  (core/pimp-workspace workspace))


(pimp-workspace workspace)

(src-paths workspace "core" true)

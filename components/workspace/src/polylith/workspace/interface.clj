(ns polylith.workspace.interface
  (:require [polylith.workspace.core :as core]
            [polylith.workspace-clj.interface :as ws-clojure]))

(def workspace (ws-clojure/read-workspace-from-disk "../clojure-polylith-realworld-example-app"))
(def workspace (ws-clojure/read-workspace-from-disk "."))
(def workspace (ws-clojure/read-workspace-from-disk "../Nova/project-unicorn" {:polylith {:top-namespace ""}}))
(def workspace (ws-clojure/read-workspace-from-disk "../ws11" {:polylith {:top-namespace ""}}))
(def workspace (ws-clojure/read-workspace-from-disk "../ws12" {:polylith {:top-namespace ""}}))

(defn pimp-workspace [workspace]
  (core/pimp-workspace workspace))

(pimp-workspace workspace)

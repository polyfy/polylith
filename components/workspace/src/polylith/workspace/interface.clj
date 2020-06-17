(ns polylith.workspace.interface
  (:require [polylith.change.interface :as change]
            [polylith.workspace.core :as core]
            [polylith.workspace.source :as source]
            [polylith.workspace.dependencies :as deps]
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

(defn resolve-libs
  "Resolves dependencies which most often are libraries."
  ([workspace]
   (deps/resolve-libs workspace nil false nil))
  ([workspace env]
   (deps/resolve-libs workspace env false nil))
  ([workspace env include-tests?]
   (deps/resolve-libs workspace env include-tests? nil))
  ([workspace env include-tests? additional-deps]
   (deps/resolve-libs workspace env include-tests? additional-deps)))

(defn pimp-workspace [workspace]
  (core/pimp-workspace workspace))

(-> workspace pimp-workspace change/with-changes)

(ns polylith.workspace.interface
  (:require [polylith.change.interface :as change]
            [polylith.workspace.core :as core]
            [polylith.workspace.source :as source]
            [polylith.workspace.dependencies :as deps]
            [polylith.workspace-clj.interface :as ws-clojure]))

; TODO: delete these and also unnecessary requires above
;(def workspace (ws-clojure/workspace-from-disk "../clojure-polylith-realworld-example-app"))
;(def workspace (ws-clojure/workspace-from-disk "."))
;(def workspace (ws-clojure/workspace-from-disk "../Nova/project-unicorn" {:polylith {:top-namespace ""}}))
;(def workspace (ws-clojure/workspace-from-disk "../ws11" {:polylith {:top-namespace ""}}))
;(def workspace (ws-clojure/workspace-from-disk "../ws12" {:polylith {:top-namespace ""}}))

(defn src-paths
  ([workspace env-group]
   (source/paths workspace env-group false))
  ([workspace env-group include-tests?]
   (source/paths workspace env-group include-tests?)))

(defn lib-paths
  ([workspace]
   (deps/paths workspace nil false nil))
  ([workspace env-group]
   (deps/paths workspace env-group false nil))
  ([workspace env-group include-tests?]
   (deps/paths workspace env-group include-tests? nil))
  ([workspace env-group include-tests? additional-deps]
   (deps/paths workspace env-group include-tests? additional-deps)))

(defn test-namespaces [workspace env-group]
  (source/test-namespaces workspace env-group))

;(defn resolve-libs
;  "Resolves dependencies which most often are libraries."
;  ([workspace]
;   (deps/resolve-libs workspace nil false nil))
;  ([workspace env-group]
;   (deps/resolve-libs workspace env-group false nil))
;  ([workspace env-group include-tests?]
;   (deps/resolve-libs workspace env-group include-tests? nil))
;  ([workspace env-group include-tests? additional-deps]
;   (deps/resolve-libs workspace env-group include-tests? additional-deps)))

(defn enrich-workspace [workspace]
  (core/enrich-workspace workspace))

; TODO: delete these
(-> "../clojure-polylith-realworld-example-app"
    ws-clojure/workspace-from-disk
    core/enrich-workspace
    change/with-changes)

;(-> workspace enrich-workspace (change/with-changes "f0a84c47c284d9502ca4cd1a017d2b4b6161bfc5"))

(ns polylith.workspace.interface
  (:require [polylith.workspace.core :as core]
            [polylith.workspace.source :as source]
            [polylith.workspace.dependencies :as deps]
            [polylith.workspace-clj.interface :as ws-clojure]))

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



;(map (juxt :name :interface-deps)
;     (:bases (->
;               ;"."
;               "../clojure-polylith-realworld-example-app"
;               ws-clojure/workspace-from-disk
;               core/enrich-workspace)))
;   ;change/with-changes)

;(def workspace (->
;                 ;"."
;                 "../clojure-polylith-realworld-example-app"
;                 ws-clojure/workspace-from-disk
;                 core/enrich-workspace))
;
;(def components (:components workspace))
;
;(first components)

;
;(map (juxt :name :lines-of-code-src :lines-of-code-test) (:components workspace))
;(map (juxt :name :lines-of-code-src :lines-of-code-test) (:bases workspace))
;
;
;(def environments (:environments workspace))
;
;(first environments)
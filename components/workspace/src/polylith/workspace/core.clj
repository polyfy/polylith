(ns polylith.workspace.core
  (:require [polylith.workspace-clj.interface :as ws-clj]))

;(ws-clj/read-workspace-from-disk "." {:polylith {:top-namespace "polylith"}})
;(ws-clj/read-workspace-from-disk "../clojure-polylith-realworld-example-app" {:polylith {:top-namespace "clojure.realworld"}})
;(ws-clj/read-workspace-from-disk "../Nova/project-unicorn" {:polylith {:top-namespace ""}})

;(def workspace (ws-clj/read-workspace-from-disk "." {:polylith {:top-namespace "polylith"}}))
;(def components (:components workspace))
;(def interface-names (vec (sort (map #(-> % :interface :name) components))))
;(def component (first components))
;(def dependencies (core/dependencies top-ns component-name component-names imports))


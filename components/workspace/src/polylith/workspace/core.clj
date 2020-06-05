(ns polylith.workspace.core
  (:require [polylith.workspace-clj.interface :as ws-clojure]
            [polylith.workspace.dependencies :as deps]
            [polylith.workspace.interfaces :as ifcs]
            [polylith.workspace.validate.validate :as validate]
            [polylith.shared.interface :as shared]))

(def workspace (ws-clojure/read-workspace-from-disk "../clojure-polylith-realworld-example-app"))
(def workspace (ws-clojure/read-workspace-from-disk "."))
(def workspace (ws-clojure/read-workspace-from-disk "../Nova/project-unicorn" {:polylith {:top-namespace ""}}))
(def workspace (ws-clojure/read-workspace-from-disk "../ws11" {:polylith {:top-namespace ""}}))
(def components (:components workspace))
;(def bases (:bases workspace))
(def interface-names (vec (sort (set (map #(-> % :interface :name) components)))))
;(def component (nth components 5))

(defn pimp-workspace [{:keys [polylith components bases] :as workspace}]
  (let [top-ns (shared/top-namespace (:top-namespace polylith))
        interfaces (ifcs/interfaces components)
        interface-names (mapv :name interfaces)
        pimped-components (mapv #(deps/with-deps top-ns interface-names %) components)
        pimped-bases (mapv #(deps/with-deps top-ns interface-names %) bases)
        warnings (validate/warnings interfaces components)
        errors (validate/errors top-ns interface-names pimped-components bases)]
    (assoc workspace :interfaces interfaces
                     :components pimped-components
                     :bases pimped-bases
                     :messages {:warnings warnings
                                :errors errors})))

(pimp-workspace workspace)

(ns polylith.workspace.core
  (:require [polylith.workspace-clj.interface :as ws-clj]
            [polylith.workspace.dependencies :as deps]
            [polylith.workspace.validate.validate :as validate]
            [polylith.shared.interface :as shared]))

(def workspace (ws-clj/read-workspace-from-disk "../clojure-polylith-realworld-example-app"))
(def workspace (ws-clj/read-workspace-from-disk "."))
(def workspace (ws-clj/read-workspace-from-disk "../Nova/project-unicorn" {:polylith {:top-namespace ""}}))
(def workspace (ws-clj/read-workspace-from-disk "../ws11" {:polylith {:top-namespace ""}}))
(def components (:components workspace))
;(def bases (:bases workspace))
(def interface-names (vec (sort (set (map #(-> % :interface :name) components)))))
;(def component (nth components 5))

(defn pimp-workspace [{:keys [polylith components bases] :as workspace}]
  (let [top-ns (shared/top-namespace (:top-namespace polylith))
        interface-names (vec (sort (set (map #(-> % :interface :name) components))))
        pimped-components (mapv #(deps/with-deps top-ns interface-names %) components)
        pimped-bases (mapv #(deps/with-deps top-ns interface-names %) bases)
        errors (validate/errors top-ns interface-names pimped-components bases)]
    (assoc workspace :components pimped-components
                     :bases pimped-bases
                     :messages {:errors errors})))

(pimp-workspace workspace)

;(snames/errors interface-names components bases)


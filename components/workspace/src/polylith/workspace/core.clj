(ns polylith.workspace.core
  (:require [polylith.workspace-clj.interface :as ws-clj]
            [polylith.workspace.interface-deps :as deps]
            [polylith.workspace.circular-deps :as cdeps]
            [polylith.workspace.shared-names :as snames]
            [polylith.shared.interface :as shared]))

;(def workspace (ws-clj/read-workspace-from-disk "../clojure-polylith-realworld-example-app"))
;(def workspace (ws-clj/read-workspace-from-disk "."))
(def workspace (ws-clj/read-workspace-from-disk "../Nova/project-unicorn" {:polylith {:top-namespace ""}}))
;(def components (:components workspace))
;(def bases (:bases workspace))
;(def interface-names (vec (sort (map #(-> % :interface :name) components))))
;(def component (nth components 5))

(defn errors [top-ns components bases interface-names]
  (vec (concat (vec (mapcat #(deps/errors top-ns % interface-names [])
                            (concat components bases)))
               (cdeps/errors interface-names components)
               (snames/errors interface-names components bases))))

(defn pimp-workspace [{:keys [polylith components bases] :as workspace}]
  (let [top-ns (shared/top-namespace (:top-namespace polylith))
        interface-names (vec (sort (map #(-> % :interface :name) components)))
        pimped-components (mapv #(deps/with-deps top-ns % interface-names) components)
        pimped-bases (mapv #(deps/with-deps top-ns % interface-names) bases)
        errors (vec (sort (errors top-ns pimped-components bases interface-names)))]
    (assoc workspace :components pimped-components
                     :bases pimped-bases
                     :messages {:errors errors})))

(pimp-workspace workspace)

;(snames/errors interface-names components bases)


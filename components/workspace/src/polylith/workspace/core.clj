(ns polylith.workspace.core
  (:require [polylith.workspace-clj.interface :as ws-clj]
            [polylith.workspace.interface-deps :as deps]
            [polylith.file.interface :as file]
            [polylith.shared.interface :as shared]))

;(ws-clj/read-workspace-from-disk "." {:polylith {:top-namespace "polylith"}})
;(ws-clj/read-workspace-from-disk "../clojure-polylith-realworld-example-app")
;(ws-clj/read-workspace-from-disk "../Nova/project-unicorn" {:polylith {:top-namespace ""}})

(def workspace (ws-clj/read-workspace-from-disk "../clojure-polylith-realworld-example-app"))
(def components (:components workspace))
(def bases (:bases workspace))
(def bricks (concat components bases))
(def interface-names (vec (sort (map #(-> % :interface :name) components))))
(def component (nth components 5))
;(def imports (:imports component))

(vec (sort (map #(-> % :interface :name) components)))

(def acomponent (assoc component :messages {:errors ["err1"] :warnings ["w"]}))

(deps/with-dependencies "clojure.realworld." component interface-names)
(deps/with-dependencies "clojure.realworld." acomponent interface-names)

{:errors []}
{:errors ["err"]
 :warnings ["warning 1"]}

(defn pimp [top-ns brick interface-names]
  (deps/with-dependencies top-ns brick interface-names))

(defn errors [top-ns brick interface-names]
  (deps/errors top-ns brick interface-names []))

(errors "clojure.realworld." component interface-names)

(vec (mapcat #(errors "clojure.realworld." % interface-names) components))
(vec (mapcat #(errors "clojure.realworld." % interface-names) bases))
(vec (mapcat #(errors "clojure.realworld." % interface-names) bricks))

(mapv #(pimp "clojure.realworld." % interface-names) components)
(mapv #(pimp "clojure.realworld." % interface-names) bases)

(defn pimp-workspace [{:keys [polylith components] :as workspace}]
  (let [top-ns (shared/top-namespace (:top-namespace polylith))
        interface-names (vec (sort (map #(-> % :interface :name) components)))
        pimped-components (mapv #(pimp top-ns % interface-names) components)
        pimped-bases (mapv #(pimp top-ns % interface-names) bases)]
    (assoc workspace :components pimped-components
                     :bases pimped-bases)))

(pimp-workspace workspace)



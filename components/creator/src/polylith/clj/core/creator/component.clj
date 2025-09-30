(ns ^:no-doc polylith.clj.core.creator.component
  (:require [polylith.clj.core.creator.brick :as brick]))

(defn create-component [ws-dir settings configs component-name interface-name dialect is-git-add]
  (let [{:keys [top-namespace interface-ns]} settings
        components-dir (str ws-dir "/components/" component-name)
        template-data (-> configs :workspace :template-data)]
    (brick/create-resources-dir ws-dir "components" component-name is-git-add)
    (brick/create-deps-config-file ws-dir "components" component-name dialect is-git-add)
    (when (= "cljs" dialect)
      (brick/create-npm-config-file ws-dir template-data "components" component-name dialect is-git-add))
    (brick/create-src-ns ws-dir "components" top-namespace components-dir interface-ns interface-name dialect is-git-add)
    (brick/create-test-ns ws-dir "components" top-namespace components-dir interface-ns interface-name interface-name dialect is-git-add)))

(defn create [{:keys [ws-dir settings configs] :as workspace} component-name interface-name dialect is-git-add]
  (let [ifc-name (or interface-name component-name)]
    (brick/create-brick workspace
                        component-name
                        (fn [] (create-component ws-dir settings configs component-name ifc-name dialect is-git-add)))))

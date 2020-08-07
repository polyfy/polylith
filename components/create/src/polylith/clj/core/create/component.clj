(ns polylith.clj.core.create.component
  (:require [polylith.clj.core.create.brick :as brick]))

(defn create-component [ws-dir settings component-name interface-name]
  (let [{:keys [top-namespace interface-ns]} settings
        components-dir (str ws-dir "/components/" component-name)]
    (brick/create-resources-dir ws-dir "components" component-name)
    (brick/create-src-ns ws-dir top-namespace components-dir interface-ns interface-name)
    (brick/create-test-ns ws-dir top-namespace components-dir interface-ns interface-name)))

(defn create [{:keys [ws-dir settings] :as workspace} component-name interface-name]
  (let [ifc-name (or interface-name component-name)]
    (brick/create-brick workspace
                        component-name
                        (fn [] (create-component ws-dir settings component-name ifc-name)))))

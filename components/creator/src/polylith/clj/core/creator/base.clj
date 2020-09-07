(ns polylith.clj.core.creator.base
  (:require [polylith.clj.core.creator.brick :as brick]))

(defn create-base [ws-dir settings base-name]
  (let [{:keys [top-namespace]} settings
        bases-dir (str ws-dir "/bases/" base-name)]
    (brick/create-resources-dir ws-dir "bases" base-name)
    (brick/create-src-ns ws-dir top-namespace bases-dir "core" base-name)
    (brick/create-test-ns ws-dir top-namespace bases-dir "core" base-name)))

(defn create [{:keys [ws-dir settings] :as workspace} base-name]
  (brick/create-brick workspace
                      base-name
                      (fn [] (create-base ws-dir settings base-name))))

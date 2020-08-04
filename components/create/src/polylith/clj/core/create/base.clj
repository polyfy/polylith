(ns polylith.clj.core.create.base
  (:require [polylith.clj.core.create.brick :as brick]))

(defn create-base [ws-dir settings base-name]
  (let [{:keys [top-namespace]} settings
        bases-dir (str ws-dir "/bases/" base-name)]
    (brick/create-resources-dir ws-dir "bases" base-name)
    (brick/brick-ns ws-dir top-namespace bases-dir "api" base-name)
    (brick/create-test-interface ws-dir top-namespace bases-dir "api" base-name)))

(defn create [ws-dir {:keys [settings] :as workspace} base-name]
  (brick/create-brick workspace
                      base-name
                      (fn [] (create-base ws-dir settings base-name))))

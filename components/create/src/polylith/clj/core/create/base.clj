(ns polylith.clj.core.create.base
  (:require [polylith.clj.core.create.brick :as brick]))

(defn create-base [current-dir settings base-name]
  (let [{:keys [top-namespace]} settings
        bases-dir (str current-dir "/bases/" base-name)]
    (brick/create-resources-dir current-dir "bases" base-name)
    (brick/create-src-interface current-dir top-namespace bases-dir "api" base-name)
    (brick/create-test-interface current-dir top-namespace bases-dir "api" base-name)))

(defn create [current-dir {:keys [settings] :as workspace} base-name]
  (when (brick/not-exists workspace base-name)
    (create-base current-dir settings base-name)))

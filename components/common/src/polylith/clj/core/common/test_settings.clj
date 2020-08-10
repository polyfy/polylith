(ns polylith.clj.core.common.test-settings
  (:require [polylith.clj.core.util.interfc.params :as params]))

(defn selected-environments [env]
  (if (coll? env)
    env
    (if (nil? env)
      []
      [env])))

(defn settings [args]
  (let [{:keys [env all all-bricks test-env]} (-> args params/extract :named-args)]
    {:run-all? (or (= "true" all)
                   (= "true" all-bricks))
     :run-env-tests? (or (= "true" all)
                         (= "true" test-env))
     :selected-environments (-> env selected-environments set)}))

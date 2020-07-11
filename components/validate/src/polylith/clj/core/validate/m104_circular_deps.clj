(ns polylith.clj.core.validate.m104-circular-deps
  (:require [clojure.string :as str]
            [polylith.clj.core.util.interfc :as util]
            [polylith.clj.core.util.interfc.color :as color]))

(defn dep [[_ {:keys [circular]}] env]
  {:env env
   :circular-deps circular})

(defn circular-dep [{:keys [name brick-deps]}]
  (util/find-first :circular-deps (map #(dep % name) brick-deps)))

(defn env-circular-deps [{:keys [env circular-deps]} color-mode]
  (let [deps (str/join " > " circular-deps)
        message (str "A circular dependency was found in the " env " environment: " deps)
        colorized-msg (str "A circular dependency was found in the " (color/environment env color-mode) " environment: " (color/component deps color-mode))]
    (when (-> circular-deps empty? not)
      [(util/ordered-map :type "error"
                         :code 104
                         :message message
                         :colorized-message colorized-msg
                         :components (vec (sort (set circular-deps)))
                         :environment env)])))

(defn errors [environments color-mode]
  (vec (mapcat #(env-circular-deps % color-mode)
               (filter identity (map #(circular-dep %) environments)))))

(ns polylith.clj.core.validate.m202-missing-libraries
  (:require [clojure.set :as set]
            [clojure.string :as str]
            [polylith.clj.core.util.interfc :as util]
            [polylith.clj.core.util.interfc.color :as color]))

(defn env-status [{:keys [name component-names base-names lib-deps]} name->used-libs ns->lib]
  (let [expected-libs (set (map ns->lib (mapcat name->used-libs (concat component-names base-names))))
        used-libs (set (map key lib-deps))
        missing-libs (set/difference expected-libs used-libs)]
    {:env name
     :missing-libraries missing-libs}))

(defn missing-lib-warning [env missing-libraries color-mode]
  (let [libs (str/join ", " (sort missing-libraries))
        message (str "Missing libraries for the " env " environment: " libs)
        colorized-msg (str "Missing libraries for the " (color/environment env color-mode) " environment: " (color/grey color-mode libs))]
    [(util/ordered-map :type "warning"
                     :code 202
                     :message message
                     :colorized-message colorized-msg
                     :environment env)]))

(defn env-warning [{:keys [env missing-libraries]} color-mode]
  (when (-> missing-libraries empty? not)
    (missing-lib-warning env missing-libraries color-mode)))

(defn warnings [environments components bases ns->lib color-mode]
  (let [name->used-libs (into {} (map (juxt :name :lib-deps) (concat bases components)))]
    (mapcat #(env-warning % color-mode)
            (map #(env-status % name->used-libs ns->lib) environments))))

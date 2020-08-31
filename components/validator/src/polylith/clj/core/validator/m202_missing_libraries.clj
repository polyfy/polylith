(ns polylith.clj.core.validator.m202-missing-libraries
  (:require [clojure.set :as set]
            [clojure.string :as str]
            [polylith.clj.core.util.interfc :as util]
            [polylith.clj.core.util.interfc.color :as color]))

(defn warning [env missing-libraries color-mode]
  (let [libs (str/join ", " (sort missing-libraries))
        message (str "Missing libraries in the " (color/environment env color-mode) " environment: " (color/grey color-mode libs))]
    [(util/ordered-map :type "warning"
                       :code 202
                       :message (color/clean-colors message)
                       :colorized-message message
                       :environment env)]))

(defn env-warning [{:keys [name component-names base-names lib-deps]} bricks color-mode]
  (let [existing-libs (set (map first lib-deps))
        brick-names (concat component-names base-names)
        brick->lib-dep-names (into {} (map (juxt :name :lib-dep-names) bricks))
        expected-libs (set (mapcat brick->lib-dep-names brick-names))
        missing-libs (set/difference expected-libs existing-libs)]
    (if (-> missing-libs empty? not)
      (warning name missing-libs color-mode))))

(defn warnings [environments components bases color-mode]
  (let [bricks (concat components bases)]
    (mapcat #(env-warning % bricks color-mode)
            environments)))

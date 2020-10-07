(ns polylith.clj.core.validator.m109-missing-libraries
  (:require [clojure.set :as set]
            [clojure.string :as str]
            [polylith.clj.core.validator.shared :as shared]
            [polylith.clj.core.util.interface :as util]
            [polylith.clj.core.util.interface.color :as color]))

(defn warning [env missing-libraries color-mode]
  (let [libs (str/join ", " (sort missing-libraries))
        message (str "Missing libraries in the " (color/environment env color-mode) " environment: " (color/grey color-mode libs))]
    [(util/ordered-map :type "error"
                       :code 109
                       :message (color/clean-colors message)
                       :colorized-message message
                       :environment env)]))

(defn env-warning [{:keys [name component-names base-names lib-deps profile]} bricks used-libs color-mode]
  (let [existing-libs (set (map first (concat lib-deps (:lib-deps profile))))
        brick-names (concat component-names base-names)
        brick->lib-dep-names (into {} (map (juxt :name :lib-dep-names) bricks))
        expected-libs (set (mapcat brick->lib-dep-names brick-names))
        missing-libs (set/intersection used-libs (set/difference expected-libs existing-libs))]
    (if (-> missing-libs empty? not)
      (warning name missing-libs color-mode))))

(defn errors [environments components bases settings color-mode]
  (let [bricks (concat components bases)
        used-libs (shared/used-libs environments settings)]
    (mapcat #(env-warning % bricks used-libs color-mode)
            environments)))

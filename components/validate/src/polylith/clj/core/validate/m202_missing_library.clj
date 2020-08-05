(ns polylith.clj.core.validate.m202-missing-library
  (:require [clojure.set :as set]
            [clojure.string :as str]
            [polylith.clj.core.common.interfc :as common]
            [polylith.clj.core.util.interfc :as util]
            [polylith.clj.core.util.interfc.color :as color]))

(defn included-in-ns? [lib-ns namespace]
  (or (= namespace lib-ns)
      (str/starts-with? namespace (common/suffix-ns-with-dot lib-ns))))

(defn brick-imports [brick-name name->brick]
  (mapcat :imports (-> brick-name name->brick :namespaces-src)))

(defn expected-ns [used-ns ns-libs]
  (util/find-first #(included-in-ns? % used-ns) ns-libs))

(defn env-status [{:keys [name component-names base-names lib-deps]} top-ns name->brick ns->lib]
  (let [brick-names (concat component-names base-names)
        ns-libs (reverse (sort (map #(-> % first str) ns->lib)))
        used-namespaces (set (filter #(not (included-in-ns? top-ns %))
                                     (mapcat #(brick-imports % name->brick) brick-names)))
        expected-libs (set (map ns->lib (set (map #(expected-ns % ns-libs) used-namespaces))))
        used-libs (set (map key lib-deps))
        missing-libs (set/difference expected-libs used-libs #{nil})]
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

(defn warnings [environments components bases ns->lib top-ns color-mode]
  (let [name->brick (into {} (map (juxt :name identity) (concat bases components)))]
    (mapcat #(env-warning % color-mode)
            (map #(env-status % top-ns name->brick ns->lib) environments))))

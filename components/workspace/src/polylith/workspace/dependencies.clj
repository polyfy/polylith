(ns polylith.workspace.dependencies
  (:require [clojure.string :as str]))

(defn brick-namespace [namespace]
  (let [idx (str/index-of namespace ".")]
    (if (nil? idx)
      namespace
      (subs namespace 0 idx))))

(defn dependency [top-ns brick path interface-names imported-ns]
  (let [import (if (str/starts-with? imported-ns top-ns)
                 (subs imported-ns (count top-ns))
                 imported-ns)
        idx (when import (str/index-of import "."))]
    (if (or (nil? idx)
            (neg? idx))
      nil
      (let [root-ns (subs import 0 idx)
            sub-ns (brick-namespace (subs import (inc idx)))]
        (when (and (contains? interface-names root-ns)
                   (not= root-ns brick))
          {:ns-path path
           :depends-on-interface root-ns
           :depends-on-ns sub-ns})))))

(defn brick-ns-dependencies [top-ns brick interface-names {:keys [ns-path imports]}]
  (filterv identity (map #(dependency top-ns brick ns-path interface-names (str %)) imports)))

(defn brick-dependencies [top-ns brick interface-names brick-imports]
  (vec (mapcat #(brick-ns-dependencies top-ns brick interface-names %) brick-imports)))

(defn error [{:keys [ns-path depends-on-interface depends-on-ns]}]
  (when ns-path
    (str "Illegal dependency on namespace '" depends-on-interface "." depends-on-ns "' in 'components" ns-path
         "'. Change to '" depends-on-interface ".interface' to solve the problem.")))

(defn dependencies [top-ns brick interface-names brick-imports]
  (let [deps (brick-dependencies top-ns brick (set interface-names) brick-imports)
        interface-deps (vec (sort (set (map :depends-on-interface deps))))
        errors (filterv identity (map error (filterv #(not= "interface" (:depends-on-ns %)) deps)))]
    {:dependencies interface-deps
     :errors errors}))

;(dependencies "polylith." "common" #{"spec" "cmd" "file" "invoice" "user"}
;              '[{:ns-path "/common/core.clj"
;                 :imports [clojure.string
;                           polylith.file.interface]}
;                {:ns-path "/common/abc.clj"
;                 :imports [clojure.core
;                           polylith.user.interface
;                           polylith.cmd.core
;                           polylith.invoice.core]}])

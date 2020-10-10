(ns polylith.clj.core.user-input.core
  (:require [clojure.string :as str]
            [polylith.clj.core.util.interface :as util]
            [polylith.clj.core.user-input.params :as params]))

(defn profile? [arg]
  (str/starts-with? arg "+"))

(defn selected-profiles [unnamed-args]
  (set (map #(subs % 1)
            (filter profile? unnamed-args))))

(defn selected-environments [env dev!]
  (let [envs (if (coll? env)
               env
               (if (nil? env)
                 []
                 [env]))]
    (set (if dev!
           (conj envs "dev")
           envs))))

(defn extract-params [args single-arg-commands]
  (let [{:keys [named-args unnamed-args]} (params/extract (rest args) single-arg-commands)
        {:keys [brick
                color-mode
                env
                fake-sha
                get
                interface
                name
                out
                since
                top-ns
                ws-dir
                ws-file
                all!
                all-bricks!
                brick!
                bricks!
                dev!
                env!
                loc!
                r!
                resources!]} named-args]
    (util/ordered-map :args (vec args)
                      :cmd (first args)
                      :get get
                      :brick brick
                      :color-mode color-mode
                      :fake-sha fake-sha
                      :interface interface
                      :is-search-for-ws-dir (contains? (set args) "::")
                      :is-all (= "true" all!)
                      :is-dev (= "true" dev!)
                      :is-show-brick brick!
                      :is-show-bricks bricks!
                      :is-show-env (= "true" env!)
                      :is-show-loc (= "true" loc!)
                      :is-run-all-brick-tests (or (= "true" all!)
                                                  (= "true" all-bricks!))
                      :is-run-env-tests (or (= "true" all!)
                                            (= "true" env!))
                      :is-show-resources (or (= "true" r!)
                                             (= "true" resources!))
                      :name name
                      :out out
                      :since since
                      :top-ns top-ns
                      :ws-dir ws-dir
                      :ws-file ws-file
                      :selected-profiles (selected-profiles unnamed-args)
                      :selected-environments (selected-environments env dev!)
                      :unnamed-args (vec unnamed-args))))

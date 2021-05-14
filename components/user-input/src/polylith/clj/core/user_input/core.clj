(ns polylith.clj.core.user-input.core
  (:require [clojure.string :as str]
            [polylith.clj.core.util.interface :as util]
            [polylith.clj.core.user-input.params :as params]))

(defn profile? [arg]
  (str/starts-with? arg "+"))

(defn selected-profiles [unnamed-args]
  (set (map #(subs % 1)
            (filter profile? unnamed-args))))

(defn selected-projects [project-name dev!]
  (let [projectx-names (if (coll? project-name)
                         project-name
                         (if (nil? project-name)
                           []
                           [project-name]))]
    (set (if dev!
           (conj projectx-names "dev")
           projectx-names))))

(defn extract-params [args single-arg-commands]
  (let [{:keys [named-args unnamed-args]} (params/extract (rest args) single-arg-commands)
        {:keys [brick
                branch
                color-mode
                project
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
                workspace!
                dev!
                project!
                loc!
                no-exit!
                r!
                resources!]} named-args]
    (util/ordered-map :args (vec args)
                      :cmd (first args)
                      :get get
                      :brick brick
                      :branch branch
                      :color-mode color-mode
                      :fake-sha fake-sha
                      :interface interface
                      :is-search-for-ws-dir (contains? (set args) "::")
                      :is-all (= "true" all!)
                      :is-dev (= "true" dev!)
                      :is-no-exit (= "true" no-exit!)
                      :is-show-brick brick!
                      :is-show-workspace workspace!
                      :is-show-project (= "true" project!)
                      :is-show-loc (= "true" loc!)
                      :is-run-all-brick-tests (or (= "true" all!)
                                                  (= "true" all-bricks!))
                      :is-run-project-tests (or (= "true" all!)
                                                (= "true" project!))
                      :is-show-resources (or (= "true" r!)
                                             (= "true" resources!))
                      :name name
                      :out out
                      :since since
                      :top-ns top-ns
                      :ws-dir ws-dir
                      :ws-file ws-file
                      :selected-profiles (selected-profiles unnamed-args)
                      :selected-projects (selected-projects project dev!)
                      :unnamed-args (vec unnamed-args))))

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
                skip
                top-ns
                ws-dir
                ws-file
                all!
                all-bricks!
                brick!
                git-add!
                latest-sha!
                workspace!
                dev!
                project!
                loc!
                no-exit!
                r!
                resources!
                user-home!
                verbose!]} named-args]
    (util/ordered-map :args (vec args)
                      :cmd (first args)
                      :get get
                      :brick brick
                      :branch branch
                      :color-mode color-mode
                      :fake-sha fake-sha
                      :user-home (when (= "true" user-home!) "USER-HOME")
                      :interface interface
                      :is-search-for-ws-dir (contains? (set args) "::")
                      :is-all (= "true" all!)
                      :is-dev (= "true" dev!)
                      :is-git-add (when git-add! (= "true" git-add!))
                      :is-no-exit (= "true" no-exit!)
                      :is-latest-sha (= "true" latest-sha!)
                      :is-show-brick (= "true" brick!)
                      :is-show-workspace (= "true" workspace!)
                      :is-show-project (= "true" project!)
                      :is-show-loc (= "true" loc!)
                      :is-run-all-brick-tests (or (= "true" all!)
                                                  (= "true" all-bricks!))
                      :is-run-project-tests (or (= "true" all!)
                                                (= "true" project!))
                      :is-show-resources (or (= "true" r!)
                                             (= "true" resources!))
                      :is-verbose (= "true" verbose!)
                      :name name
                      :out out
                      :since since
                      :skip (when skip (if (vector? skip) skip [skip]))
                      :top-ns top-ns
                      :ws-dir ws-dir
                      :ws-file ws-file
                      :selected-profiles (selected-profiles unnamed-args)
                      :selected-projects (selected-projects project dev!)
                      :unnamed-args (vec unnamed-args))))

(ns polylith.clj.core.user-input.core
  (:require [clojure.string :as str]
            [polylith.clj.core.util.interface :as util]
            [polylith.clj.core.user-input.params :as params]))

(defn profile? [arg]
  (str/starts-with? arg "+"))

(defn selected-profiles [unnamed-args]
  (set (map #(subs % 1)
            (filter profile? unnamed-args))))

(defn selected-projects
  "If we pass in e.g project:p1:p2, then that means we should only
   run tests for these projects (and if :all is passed in, we should
   run all the tests for selected projects). We normally don't include
   the development project, but we can pass in :dev as a way to include it."
  [project-name]
  (set (if (coll? project-name)
         project-name
         (if (nil? project-name)
           []
           [project-name]))))

(defn from-to [[from to]]
  {:from from
   :to to})

(defn replace-from-to [replace]
  (when (and (vector? replace))
    (mapv from-to (partition 2 replace))))

(defn extract [args]
  {:cmd (first args)
   :params (-> args rest vec)})

(defn extract-params [args single-arg-commands]
  (let [{:keys [cmd params]} (extract args)
        {:keys [named-args unnamed-args]} (params/extract params single-arg-commands)
        {:keys [brick
                branch
                color-mode
                dir
                file
                project
                fake-sha
                get
                interface
                name
                out
                replace
                since
                skip
                top-ns
                ws-dir
                ws-file
                all!
                all-bricks!
                brick!
                compact!
                git-add!
                latest-sha!
                no-changes!
                tap!
                workspace!
                dev!
                project!
                loc!
                no-exit!
                r!
                resources!
                verbose!]} named-args]
    (util/ordered-map :args (vec args)
                      :cmd cmd
                      :get get
                      :branch branch
                      :color-mode color-mode
                      :dir dir
                      :file file
                      :fake-sha fake-sha
                      :interface interface
                      :is-tap (= "true" tap!)
                      :is-search-for-ws-dir (contains? (set args) "::")
                      :is-all (= "true" all!)
                      :is-compact (= "true" compact!)
                      :is-dev (= "true" dev!)
                      :is-git-add (when git-add! (= "true" git-add!))
                      :is-latest-sha (= "true" latest-sha!)
                      :is-no-changes (= "true" no-changes!)
                      :is-no-exit (= "true" no-exit!)
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
                      :replace (replace-from-to replace)
                      :since since
                      :skip (when skip (if (vector? skip) skip [skip]))
                      :top-ns top-ns
                      :ws-dir ws-dir
                      :ws-file ws-file
                      :selected-bricks (when brick (if (vector? brick) brick [brick]))
                      :selected-profiles (selected-profiles unnamed-args)
                      :selected-projects (selected-projects project)
                      :unnamed-args (vec unnamed-args))))

(ns ^:no-doc polylith.clj.core.user-input.core
  (:require [clojure.string :as str]
            [polylith.clj.core.util.interface :as util]
            [polylith.clj.core.user-input.args :as args]))

(defn profile? [arg]
  (str/starts-with? arg "+"))

(defn selected-profiles [unnamed-args]
  (set (map #(subs % 1)
            (filter profile? unnamed-args))))

(defn selected-projects
  "If we pass in e.g. project:p1:p2, then that means we should only
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
   :args (-> args rest vec)})

(defn as-value [arg]
  (when arg
    (if (vector? arg)
      (first arg)
      arg)))

(defn as-vector [arg]
  (when arg
    (if (vector? arg)
      arg
      [arg])))

(defn as-more [arg]
  (let [argument (as-vector arg)]
    (if (and (seq argument)
             (-> argument last empty?))
      (vec (drop-last argument))
      argument)))

(defn clean-unnamed-args [cmd unnamed-args]
  (if (= "update" cmd)
    (filterv #(and (not (str/blank? %))
                   (not= "update" %))
             unnamed-args)
    (filterv #(not (str/blank? %))
             unnamed-args)))

(defn extract-arguments [arguments single-arg-commands]
  (let [{:keys [cmd args]} (extract arguments)
        {:keys [named-args unnamed-args]} (args/extract args single-arg-commands)
        {:keys [all!
                all-bricks!
                brick
                branch
                brick!
                color-mode
                commit!
                changed-files
                compact!
                dir
                dev!
                fake-poly!
                fake-sha
                fake-tag
                file
                get
                git-add!
                github!
                help
                interface
                latest-sha!
                loc!
                local!
                more
                name
                no-changes!
                no-exit!
                out
                outdated!
                page
                project
                project!
                r!
                replace
                resources!
                since
                skip
                tap!
                top-ns
                update
                verbose!
                workspace!
                ws
                ws-dir
                ws-file]} named-args]
    (util/ordered-map :args (vec arguments)
                      :cmd cmd
                      :get get
                      :branch branch
                      :color-mode (as-value color-mode)
                      :changed-files (as-vector changed-files)
                      :dir dir
                      :file file
                      :fake-sha fake-sha
                      :fake-tag fake-tag
                      :help (as-value help)
                      :interface interface
                      :is-all (= "true" all!)
                      :is-commit (= "true" commit!)
                      :is-compact (= "true" compact!)
                      :is-dev (= "true" dev!)
                      :is-fake-poly (= "true" fake-poly!)
                      :is-git-add (when git-add! (= "true" git-add!))
                      :is-github (= "true" github!)
                      :is-latest-sha (= "true" latest-sha!)
                      :is-local (= "true" local!)
                      :is-no-changes (= "true" no-changes!)
                      :is-no-exit (= "true" no-exit!)
                      :is-outdated (= "true" outdated!)
                      :is-update (boolean (or (seq update)
                                              (contains? (set unnamed-args) "update")))
                      :is-run-all-brick-tests (or (= "true" all!)
                                                  (= "true" all-bricks!))
                      :is-run-project-tests (or (= "true" all!)
                                                (= "true" project!))
                      :is-search-for-ws-dir (contains? (set arguments) "::")
                      :is-show-brick (= "true" brick!)
                      :is-show-loc (= "true" loc!)
                      :is-show-project (= "true" project!)
                      :is-show-resources (or (= "true" r!)
                                             (= "true" resources!))
                      :is-show-workspace (= "true" workspace!)
                      :is-tap (= "true" tap!)
                      :is-verbose (= "true" verbose!)
                      :more (as-more more)
                      :name name
                      :out (as-value out)
                      :page (as-value page)
                      :replace (replace-from-to replace)
                      :selected-bricks (as-vector brick)
                      :selected-profiles (selected-profiles unnamed-args)
                      :selected-projects (selected-projects project)
                      :since since
                      :skip (as-vector skip)
                      :top-ns top-ns
                      :update (as-vector update)
                      :ws (as-value ws)
                      :ws-dir ws-dir
                      :ws-file ws-file
                      :unnamed-args (clean-unnamed-args cmd unnamed-args))))

(ns polylith.clj.core.shell.candidate.specification
  [:require [polylith.clj.core.common.interface :as common]
            [polylith.clj.core.shell.candidate.creators :as c]
            [polylith.clj.core.shell.candidate.selector.remote-branches :as remote-branches]
            [polylith.clj.core.shell.candidate.selector.ws-bricks :as ws-bricks]
            [polylith.clj.core.shell.candidate.selector.ws-explore :as ws-explore]
            [polylith.clj.core.shell.candidate.selector.file-explorer :as file-explorer]
            [polylith.clj.core.shell.candidate.selector.ws-tag-patterns :as ws-tag-patterns]
            [polylith.clj.core.shell.candidate.selector.ws-deps-entities :as ws-deps-entities]
            [polylith.clj.core.shell.candidate.selector.ws-projects-to-test :as ws-projects-to-test]]
  (:refer-clojure :exclude [load test]))

;; check
(def check (c/single-txt "check"))

;; create
(def create-base-name (c/multi-param "name"))
(def create-base (c/single-txt "base" [create-base-name]))
(def interface-value (c/multi-arg :create-component "interface"))
(def interface (c/multi-param "interface" 2 (c/group :create-component) (c/optional) [interface-value]))
(def create-component-name-value (c/multi-arg :create-component "name"))
(def create-component-name (c/multi-param "name" 1 (c/group :create-component) [create-component-name-value]))
(def create-component (c/single-txt "component" :create-component [create-component-name interface]))
(def create-project-name (c/multi-param "name"))
(def create-project (c/single-txt "project" [create-project-name]))
(def create (c/single-txt "create" :create [create-base create-component create-project]))

;; deps
(def deps-brick (c/fn-explorer "brick" :deps #'ws-deps-entities/select-bricks))
(def deps-project (c/fn-explorer "project" :deps #'ws-deps-entities/select-projects))
(def deps (c/single-txt "deps" :deps [deps-brick deps-project]))

;; diff
(def diff-since (c/fn-explorer "since" :diff #'ws-tag-patterns/select))
(def diff (c/single-txt "diff" :diff [diff-since]))

;; help
(def help-create-base (c/single-txt "base"))
(def help-create-component (c/single-txt "component"))
(def help-create-project (c/single-txt "project"))
(def help-create-workspace (c/single-txt "workspace"))
(def help-create (c/single-txt "create" [help-create-base help-create-component help-create-project help-create-workspace]))
(def help-deps-project (c/flag "project" :help-deps))
(def help-deps-brick (c/flag "brick" :help-deps))
(def help-deps (c/single-txt "deps" :help-deps [help-deps-brick help-deps-project]))
(def help (c/single-txt "help" (vec (concat [help-create help-deps]
                                            (mapv #(c/single-txt %)
                                                  ["check" "diff" "info" "libs" "switch-ws" "shell" "tap" "test" "version" "ws"])))))

;; info
(def info-since (c/fn-explorer "since" :info #'ws-tag-patterns/select))
(def info-project (c/fn-explorer "project" :info #'ws-projects-to-test/select))
(def info-brick (c/fn-explorer "brick" :info #'ws-bricks/select))
(def info-resources (c/flag "resources" :info))
(def info-project-flag (c/flag-explicit "project" :info))
(def info-dev (c/flag "dev" :info))
(def info-loc (c/flag "loc" :info))
(def info-all-bricks (c/flag "all-bricks" :info))
(def info-all (c/flag "all" :info))

(defn info [profiles]
  (c/single-txt "info" :info
                (concat profiles
                        [info-all info-all-bricks info-brick info-loc info-dev
                         info-resources info-project info-project-flag info-since])))

;; libs
(def libs (c/single-txt "libs" :libs))

;; test
(def test-since (c/fn-explorer "since" :test #'ws-tag-patterns/select))
(def test-project (c/fn-explorer "project" :test #'ws-projects-to-test/select))
(def test-brick (c/fn-explorer "brick" :test #'ws-bricks/select))
(def test-project-flag (c/flag-explicit "project" :test))
(def test-dev (c/flag "dev" :test))
(def test-loc (c/flag "loc" :test))
(def test-verbose (c/flag "verbose" :test))
(def test-all-bricks (c/flag "all-bricks" :test))
(def test-all (c/flag "all" :test))

(defn test [profiles]
  (c/single-txt "test" :test
                (vec (concat [test-all test-all-bricks test-brick test-loc test-verbose
                              test-dev test-project test-project-flag test-since]
                             profiles))))

;; version
(def version (c/single-txt "version"))

;; migrate
(defn migrate [show-migrate?]
  (when show-migrate?
    [(c/single-txt "migrate")]))

(def ws-branch (c/fn-explorer "branch" :ws #'remote-branches/select))
(def ws-project (c/multi-fn "project" (c/group :ws) (c/function #'ws-projects-to-test/select)))
(def ws-brick (c/multi-fn "brick" (c/group :ws) (c/function #'ws-bricks/select)))
(def ws-project-flag (c/flag-explicit "project" :ws))
(def ws-dev (c/flag "dev" :ws))
(def ws-latest-sha (c/flag "latest-sha" :ws))
(def ws-loc (c/flag "loc" :ws))
(def ws-all-bricks (c/flag "all-bricks" :ws))
(def ws-all (c/flag "all" :ws))
(def ws-since (c/fn-explorer "since" :ws #'ws-tag-patterns/select))
(def ws-out (c/fn-explorer "out" :ws #'file-explorer/select))
(def ws-get (c/multi-fn "get" (c/group :ws) (c/function #'ws-explore/select)))

;; ws
(defn ws [profiles]
  (c/single-txt "ws" :ws
                (vec (concat [ws-project ws-brick ws-project-flag ws-dev ws-latest-sha
                              ws-loc ws-all-bricks ws-all ws-get ws-out ws-since ws-branch]
                             profiles))))

(def switch-ws-dir (c/fn-explorer "dir" :switch-ws #'file-explorer/select))
(def switch-ws-file (c/fn-explorer "file" :switch-ws #'file-explorer/select))
(def switch-ws (c/single-txt "switch-ws" :switch-ws [switch-ws-file switch-ws-dir]))

(defn profiles [group-id settings]
  (map #(c/group-arg (str "+" %) group-id (str "+" %))
       (-> settings :profile-to-settings keys)))

(defn candidates [{:keys [settings user-input] :as workspace}]
  (let [{:keys [ws-dir ws-file]} user-input
        show-migrate? (common/toolsdeps1? workspace)
        info-profiles (profiles :info settings)
        test-profiles (profiles :test settings)
        ws-profiles (profiles :ws settings)
        current-ws? (or (nil? ws-file)
                        (or (nil? ws-dir)
                            (= "." ws-dir)))]
    (vec (concat [check
                  deps
                  diff
                  help
                  libs
                  version
                  switch-ws
                  (info info-profiles)
                  (ws ws-profiles)]
                 (migrate show-migrate?)
                 (if current-ws?
                   [create
                    (test test-profiles)]
                   [])))))

;; create workspace
(def create-workspace-commit (c/flag "commit" :create-workspace))
(def create-workspace-branch (c/multi-param "branch"))
(def create-workspace-top-ns-value (c/group-arg "" :create-workspace "top-ns" false))
(def create-workspace-top-ns (c/multi-param "top-ns" (c/group :create-workspace) [create-workspace-top-ns-value]))
(def create-workspace-name-value (c/group-arg "" :create-workspace "name" false))
(def create-workspace-name (c/multi-param "name" 1 (c/group :create-workspace) [create-workspace-name-value]))
(def create-workspace (c/single-txt "workspace" :create-workspace [create-workspace-name create-workspace-top-ns create-workspace-branch create-workspace-commit]))
(def create-outside-ws-root (c/single-txt "create" [create-workspace]))

(def candidates-outside-ws-root [help version create-outside-ws-root])

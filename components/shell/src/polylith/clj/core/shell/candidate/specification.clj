(ns ^:no-doc polylith.clj.core.shell.candidate.specification
  [:require [polylith.clj.core.shell.candidate.creators :as c]
            [polylith.clj.core.shell.candidate.selector.color-modes :as color-modes]
            [polylith.clj.core.shell.candidate.selector.doc.help :as doc-help]
            [polylith.clj.core.shell.candidate.selector.doc.more :as doc-more]
            [polylith.clj.core.shell.candidate.selector.doc.page :as doc-page]
            [polylith.clj.core.shell.candidate.selector.doc.ws :as doc-ws]
            [polylith.clj.core.shell.candidate.selector.file-explorer :as file-explorer]
            [polylith.clj.core.shell.candidate.selector.remote-branches :as remote-branches]
            [polylith.clj.core.shell.candidate.selector.outdated-libs :as outdated-libs]
            [polylith.clj.core.shell.candidate.selector.with-test-configs :as with-test-configs]
            [polylith.clj.core.shell.candidate.selector.ws-bricks :as ws-bricks]
            [polylith.clj.core.shell.candidate.selector.ws-explore :as ws-explore]
            [polylith.clj.core.shell.candidate.selector.ws-shortcuts :as ws-shortcuts]
            [polylith.clj.core.shell.candidate.selector.ws-tag-patterns :as ws-tag-patterns]
            [polylith.clj.core.shell.candidate.selector.ws-deps-entities :as ws-deps-entities]
            [polylith.clj.core.shell.candidate.selector.ws-projects :as ws-projects]
            [polylith.clj.core.shell.candidate.selector.ws-projects-to-test :as ws-projects-to-test]
            [polylith.clj.core.system.interface :as system]
            [polylith.clj.core.user-config.interface :as user-config]]
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
(def create-workspace-commit (c/flag "commit" :create-workspace))
(def create-workspace-branch (c/multi-param "branch"))
(def create-workspace-top-ns-value (c/group-arg "" :create-workspace "top-ns" false))
(def create-workspace-top-ns (c/multi-param "top-ns" (c/group :create-workspace) [create-workspace-top-ns-value]))
(def create-workspace-name-value (c/group-arg "" :create-workspace "name" false))
(def create-workspace-name (c/multi-param "name" 1 (c/group :create-workspace) [create-workspace-name-value]))
(def create-workspace (c/single-txt "workspace" :create-workspace [create-workspace-name create-workspace-top-ns create-workspace-branch create-workspace-commit]))

(defn create [current-ws? all?]
  (when current-ws?
    (c/single-txt "create" :create
      (concat [create-base create-component create-project]
              (when all? [create-workspace])))))

(def compact (c/flag "compact" :compact))

;; deps
(def deps-swap-axes (c/flag "swap-axes" :deps))
(def deps-brick (c/fn-explorer "brick" :deps #'ws-deps-entities/select-bricks))
(def deps-project (c/fn-explorer "project" :deps #'ws-deps-entities/select-projects))
(def deps-out (c/fn-explorer "out" :deps (file-explorer/select-fn)))
(def deps-transparent (c/flag "transparent" :deps))
(def deps-color-mode (c/fn-values "color-mode" :deps #'color-modes/select))

(defn deps [all? extended?]
  (c/single-txt "deps" :deps
    (concat [deps-swap-axes deps-brick deps-project deps-color-mode]
            (when all? [compact])
            (when extended? [deps-transparent])
            (when (or all? extended?) [deps-out]))))

;; diff
(def diff-since (c/fn-explorer "since" :diff #'ws-tag-patterns/select))
(def diff (c/single-txt "diff" :diff [diff-since]))

;; doc
(def doc-help (c/fn-values "help" :doc #'doc-help/select))
(def doc-more (c/fn-values "more" :doc #'doc-more/select))
(def doc-page (c/fn-values "page" :doc #'doc-page/select))
(def doc-ws (c/fn-values "ws" :doc #'doc-ws/select))
(def doc-github (c/flag "github" :doc))
(def doc-local (c/flag "local" :doc))
(def doc-branch (c/fn-explorer "branch" :doc #'remote-branches/select))

(defn doc [all? local?]
  (c/single-txt "doc" :doc
                (concat [doc-help doc-more doc-page doc-ws]
                        (when all? [doc-branch])
                        (when (or all? local?) [doc-github])
                        ;; If starting a shell with :local, don't suggest :local
                        (when (and all? (not local?)) [doc-local]))))

;; help
(def help-all (c/flag "all" :help))
(def help-create-base (c/single-txt "base"))
(def help-create-component (c/single-txt "component"))
(def help-create-project (c/single-txt "project"))
(def help-create-workspace (c/single-txt "workspace"))
(def help-create (c/single-txt "create" [help-create-base help-create-component help-create-project help-create-workspace]))
(def help-deps-project (c/flag "project" :help-deps))
(def help-deps-workspace (c/flag "workspace" :help-deps))
(def help-deps-brick (c/flag "brick" :help-deps))
(def help-deps (c/single-txt "deps" :help-deps [help-deps-brick help-deps-project help-deps-workspace]))
(def help-fake-poly (c/flag "fake-poly" :help-deps))

(defn help [all?]
  (c/single-txt "help" (vec (concat [help-create help-deps]
                                    (when all? [help-all help-fake-poly])
                                    (mapv #(c/single-txt %)
                                          (concat ["check" "diff" "info" "libs" "switch-ws" "shell" "tap" "test" "version" "ws"]
                                                  (if system/extended? ["overview"] [])))))))

;; info
(def info-fake-sha (c/multi-param "fake-sha"))
(def info-fake-tag (c/multi-param "fake-tag"))
(def info-changed-files (c/fn-explorer "changed-files" :info #'file-explorer/select-all))
(def info-since (c/fn-explorer "since" :info #'ws-tag-patterns/select))
(def info-project (c/fn-explorer "project" :info #'ws-projects-to-test/select))
(def info-brick (c/fn-explorer "brick" :info #'ws-bricks/select))
(def info-transparent (c/flag "transparent" :info))
(def info-no-changes (c/flag "no-changes" :info))
(def info-resources (c/flag "resources" :info))
(def info-out (c/fn-explorer "out" :info (file-explorer/select-fn)))
(def info-project-flag (c/flag-explicit "project" :info))
(def info-dev (c/flag "dev" :info))
(def info-loc (c/flag "loc" :info))
(def info-all-bricks (c/flag "all-bricks" :info))
(def info-all (c/flag "all" :info))
(def info-skip (c/fn-explorer "skip" :info #'ws-projects/select))
(def info-color-mode (c/fn-values "color-mode" :info #'color-modes/select))

(defn info [profiles all? extended?]
  (c/single-txt "info" :info
    (concat profiles
            [info-all info-all-bricks info-brick info-loc info-dev info-resources
             info-project info-project-flag info-since info-color-mode]
            (when all? [info-fake-sha info-fake-tag info-changed-files info-skip info-no-changes])
            (when extended? [info-transparent])
            (when (or all? extended?) [info-out]))))

;; libs
(def libs-outdated (c/flag "outdated" :libs))
(def libs-update (c/flag "update" :libs))
(def libs-hide-lib-size (c/flag "hide-lib-size" :libs))
(def libs-libraries (c/fn-explorer "libraries" :libs #'outdated-libs/select))
(def libs-out (c/fn-explorer "out" :libs (file-explorer/select-fn)))
(def libs-skip (c/fn-explorer "skip" :libs #'ws-projects/select))
(def libs-transparent (c/flag "transparent" :libs))
(def libs-color-mode (c/fn-values "color-mode" :libs #'color-modes/select))

(defn libs [all? extended?]
  (c/single-txt "libs" :libs
    (concat [libs-outdated libs-update libs-libraries libs-color-mode]
            (when all? [compact libs-skip libs-hide-lib-size])
            (when extended? [libs-transparent])
            (when (or all? extended?) [libs-out]))))

;; test
(def test-since (c/fn-explorer "since" :test #'ws-tag-patterns/select))
(def test-project (c/fn-explorer "project" :test #'ws-projects-to-test/select))
(def test-brick (c/fn-explorer "brick" :test #'ws-bricks/select))
(def test-fail-if-nothing-to-test-flag (c/flag "fail-if-nothing-to-test" :test))
(def test-project-flag (c/flag-explicit "project" :test))
(def test-dev (c/flag "dev" :test))
(def test-loc (c/flag "loc" :test))
(def test-verbose (c/flag "verbose" :test))
(def test-skip (c/fn-explorer "skip" :test #'ws-projects/select))
(def test-all-bricks (c/flag "all-bricks" :test))
(def test-all (c/flag "all" :test))
(def test-with (c/fn-explorer "with" :test #'with-test-configs/select))

(defn test [profiles current-ws? has-test-configs? all?]
  (when current-ws?
    (c/single-txt "test" :test
      (vec (concat [test-all test-all-bricks test-brick test-loc test-verbose
                    test-dev test-project test-project-flag test-since]
                   (when has-test-configs? [test-with])
                   (when all? [test-skip test-fail-if-nothing-to-test-flag])
                   profiles)))))

;; overview
(def overview-out (c/fn-explorer "out" :overview (file-explorer/select-fn)))
(def overview-transparent (c/flag "transparent" :overview))
(def overview-no-changes (c/flag "no-changes" :overview))
(def overview-color-mode (c/fn-values "color-mode" :overview #'color-modes/select))
(def overview (c/single-txt "overview" :overview [overview-out overview-transparent overview-no-changes overview-color-mode]))

;; version
(def version (c/single-txt "version"))

(def ws-branch (c/fn-explorer "branch" :ws #'remote-branches/select))
(def ws-with (c/fn-explorer "project" :ws #'ws-deps-entities/select-projects))
(def ws-replace (c/multi-param "replace"))
(def ws-project (c/fn-explorer "project" :ws #'ws-projects-to-test/select))
(def ws-brick (c/fn-explorer "brick" :ws #'ws-bricks/select))
(def ws-project-flag (c/flag-explicit "project" :ws))
(def ws-dev (c/flag "dev" :ws))
(def ws-latest-sha (c/flag "latest-sha" :ws))
(def ws-loc (c/flag "loc" :ws))
(def ws-outdated (c/flag "outdated" :ws))
(def ws-all-bricks (c/flag "all-bricks" :ws))
(def ws-all (c/flag "all" :ws))
(def ws-since (c/fn-explorer "since" :ws #'ws-tag-patterns/select))
(def ws-out (c/fn-explorer "out" :ws #'file-explorer/select-edn))
(def ws-get (c/fn-explorer "get" :ws #'ws-explore/select))
(def ws-no-changes (c/flag "no-changes" :ws))
(def ws-color-mode (c/fn-values "color-mode" :ws #'color-modes/select))
(def ws-with (c/fn-explorer "with" :ws #'with-test-configs/select))

;; ws
(defn ws [profiles has-test-configs? all?]
  (c/single-txt "ws" :ws
    (vec (concat [ws-project ws-brick ws-project-flag ws-dev ws-latest-sha
                  ws-loc ws-all-bricks ws-all ws-get ws-out ws-since]
                 profiles
                 (when has-test-configs? [ws-with])
                 (when all? [ws-branch ws-outdated ws-replace ws-no-changes ws-color-mode])))))

;; switch-ws
(def switch-ws-dir (c/fn-explorer "dir" :switch-ws #'file-explorer/select-edn))
(def switch-ws-file (c/fn-explorer "file" :switch-ws #'file-explorer/select-edn))
(def switch-ws-via (c/fn-explorer "via" :switch-ws #'ws-shortcuts/select))

(defn switch-ws [ws-shortcuts]
  (c/single-txt "switch-ws" :switch-ws
                (concat [switch-ws-file switch-ws-dir]
                        (when ws-shortcuts [switch-ws-via]))))

(defn ->profiles [group-id profiles all?]
  (let [profile-keys (map :name profiles)]
    (when (seq profile-keys)
      (concat (when all? [(c/group-arg "+" group-id "+")])
              (map #(c/group-arg (str "+" %) group-id (str "+" %))
                   profile-keys)))))

(defn candidates [{:keys [configs profiles user-input]}]
  (let [{:keys [ws-dir ws-file is-all is-local]} user-input
        ws-shortcuts (user-config/ws-shortcuts-paths)
        info-profiles (->profiles :info profiles is-all)
        test-profiles (->profiles :test profiles is-all)
        ws-profiles (->profiles :ws profiles is-all)
        has-test-configs? (seq (-> configs :workspace :test-configs))
        current-ws? (or (nil? ws-file)
                        (or (nil? ws-dir)
                            (= "." ws-dir)))]
    (vec (concat [check
                  diff
                  version
                  (switch-ws ws-shortcuts)
                  (create current-ws? is-all)
                  (deps is-all system/extended?)
                  (doc is-all is-local)
                  (help is-all)
                  (info info-profiles is-all system/extended?)
                  (libs is-all system/extended?)
                  (test test-profiles current-ws? has-test-configs? is-all)
                  (ws ws-profiles has-test-configs? is-all)
                  (when system/extended? overview)]))))

(def create-outside-ws-root (c/single-txt "create" [create-workspace]))

(defn candidates-outside-ws-root []
  [(help false)
   version
   create-outside-ws-root
   (doc false false)
   (switch-ws (user-config/ws-shortcuts-paths))])

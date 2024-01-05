(ns create-example
  (:require [babashka.fs :as fs]
            [babashka.http-client :as http]
            [cheshire.core :as json]
            [clojure.edn :as edn]
            [clojure.string :as str]
            [lread.status-line :as status]
            [borkdude.rewrite-edn :as r]
            [shell :as sh]
            [version-clj.core :as v]))

(defn assert-tools-installed []
  (status/line :head "Checking for required tools")
  (let [missing-tools (remove fs/which ["git" "tree" "clojure"])]
    (if (seq missing-tools)
      (status/die 1 "required tools not found: %s" (str/join "," missing-tools))
      (status/line :detail "All required tools found"))))

(defn assert-min-tree-version []
  (status/line :head "Checking tree version")
  (let [min-version "2.1.1"
        tree-exe (fs/which "tree")
        actual-version (->> (sh/shell {:out :string}
                                      "tree --version")
                            :out
                            (re-find #"^tree v(.*?) ")
                            second)]
    (if (v/older? actual-version min-version)
      (status/die 1 "Found %s version %s, must be at least version %s" tree-exe actual-version min-version)
      (println (format "Found %s version %s" tree-exe actual-version)))))

(defn latest-available-clojure-version []
  (-> (http/get "https://api.github.com/repos/clojure/brew-install/releases/latest")
      :body
      (json/parse-string true)
      :tag_name))

(defn assert-clojure-is-latest []
  (status/line :head "Checking clojure")
  (let [clojure-exe (fs/which "clojure")
        actual-version (-> (sh/shell {:out :string}
                                     clojure-exe "-Sdescribe")
                           :out
                           edn/read-string
                           :version)
        expected-version (latest-available-clojure-version)]
    (if (= actual-version expected-version)
      (println (format "Found %s version %s, it is latest available version" clojure-exe actual-version))
      (status/die 1 "Found %s version %s,\nPlease upgrade to version %s" clojure-exe actual-version expected-version))))

(defn download-deps []
  (status/line :head "Downloading deps")
  (sh/shell "clojure -A:dev:test -P"))

(defn output-dir-tree [from-dir dir out-file]
  (sh/shell {:dir from-dir :out out-file}
            "tree -F" dir))

(defn copy [& from-tos]
  (doseq [[from to] from-tos]
    (println (format "copying %s %s" from to))
    (fs/copy from to {:replace-existing true})))

(defn test-result->output
  "Generalize specifics in test output and add exit code"
  [{:keys [out exit]}]
  (-> out
      (str/replace #"(?m)^(Execution time: )[0-9]+.*$" "$1x seconds")
      (str "\n# Exit code: " exit "\n")))

(defn fn-default-opts [f default-opts]
  (fn [& args]
    (let [[opts & args] (if (map? (first args))
                          args
                          (conj args {}))
          opts (merge default-opts opts)]
      (apply f opts args))))

(defn polys
  "Run poly once to generate txt and a second time to generate image.
  Note that not all poly cmds support output to image."
  [{:keys [ws-dir output-dir images-dir]} poly-cmd-str fname-txt fname-png]
  (sh/poly {:out (fs/file output-dir fname-txt) :dir ws-dir}
           (format "%s color-mode:none" poly-cmd-str))
  (sh/polyx {:dir ws-dir}
            (format "%s out:%s" poly-cmd-str (fs/file images-dir fname-png))))

(defn poly-infos
  "Run poly info once to generate txt and a second time to generate image"
  [{:keys [fake-sha] :as opts} info-arg-str fname-txt fname-png]
  (polys opts (format "info %s fake-sha:%s" info-arg-str fake-sha)
         fname-txt fname-png))

;; task fns

(defn gen-nav []
  (sh/shell "clojure -M:gen-nav"))

(defn workspace [{:keys [ws-parent-dir output-dir]}]
  (fs/create-dir ws-parent-dir)
  (sh/poly {:dir ws-parent-dir}
           "create workspace name:example top-ns:se.example :git-add :commit")
  (output-dir-tree ws-parent-dir "example" (fs/file output-dir "workspace-tree.txt")))

(defn development [{:keys [ws-dir sections-dir]}]
  (fs/create-dirs (fs/file ws-dir "development/src/dev"))
  (copy [(fs/file sections-dir "development/lisa.clj") (fs/file ws-dir "development/src/dev")])
  (sh/shell {:dir ws-dir} "git add development/src/dev/lisa.clj"))

(defn component [{:keys [ws-dir fake-sha images-dir sections-dir output-dir]}]
  (let [poly (fn-default-opts sh/poly {:dir ws-dir})
        shell (fn-default-opts sh/shell {:dir ws-dir})
        polyx (fn-default-opts sh/polyx {:dir ws-dir})]
    (poly "create component name:user")
    (output-dir-tree (fs/file ws-dir "..") "example" (fs/file output-dir "component-tree.txt"))
    (copy [(fs/file sections-dir "component/workspace.edn")      ws-dir]
          [(fs/file sections-dir "component/deps.edn")           ws-dir]
          [(fs/file sections-dir "component/user-core.clj")      (fs/file ws-dir "components/user/src/se/example/user/core.clj")]
          [(fs/file sections-dir "component/user-interface.clj") (fs/file ws-dir "components/user/src/se/example/user/interface.clj")])
    (shell "git add components/user/src/se/example/user/core.clj")
    (poly {:out (fs/file output-dir "component-info.txt")}
          (format "info fake-sha:%s color-mode:none" fake-sha))
    (polyx (format "info fake-sha:%s out:%s" fake-sha (fs/file images-dir "component/output/info.png")))))

(defn base [{:keys [ws-dir sections-dir output-dir]}]
  (sh/poly {:dir ws-dir} "create base name:cli")
  (output-dir-tree (fs/file ws-dir "..") "example" (fs/file output-dir "base-tree.txt"))
  (copy [(fs/file sections-dir "base/deps.edn")     ws-dir]
        [(fs/file sections-dir "base/cli-core.clj") (fs/file ws-dir "bases/cli/src/se/example/cli/core.clj")]))

(defn project [{:keys [ws-dir sections-dir output-dir]}]
  (sh/poly {:dir ws-dir} "create project name:command-line alias:cl")
  (output-dir-tree (fs/file ws-dir "..") "example" (fs/file output-dir "project-tree.txt"))
  (copy [(fs/file sections-dir "project/deps.edn")              ws-dir]
        [(fs/file sections-dir "project/command-line-deps.edn") (fs/file ws-dir "projects/command-line/deps.edn")]))

(defn polyx [{:keys [ws-dir fake-sha images-dir] :as opts}]
  (poly-infos opts "" "polyx-info.txt" "polyx/output/info.png")
  (sh/polyx {:dir ws-dir}
            (format "overview :no-changes fake-sha:%s out:%s" fake-sha (fs/file images-dir "polyx/output/overview.png"))))

(defn tools-deps [_]
  (status/line :detail "Nothing to do for now"))

(defn build [{:keys [ws-dir sections-dir]}]
  (copy [(fs/file sections-dir "build/deps.edn")              ws-dir]
        [(fs/file sections-dir "build/build.clj")             ws-dir]
        [(fs/file sections-dir "build/command-line-deps.edn") (fs/file ws-dir "projects/command-line/deps.edn")])

  (sh/shell {:dir ws-dir} "clojure -T:build uberjar :project command-line")
  (sh/run-java-jar {:dir (fs/file ws-dir "projects/command-line/target")} "command-line.jar" "Lisa"))

(defn git [{:keys [ws-dir output-dir] :as opts}]
  (let [shell (fn-default-opts sh/shell {:dir ws-dir})
        poly (fn-default-opts sh/poly {:dir ws-dir})]
    (poly-infos opts "" "git-info.txt" "git/output/info.png")
    (shell "git --no-pager log")
    (shell "git add --all")
    (poly {:out (fs/file output-dir "git-diff.txt")} "diff")
    (shell "git commit -m 'Created the user and cli bricks.")
    (shell "git --no-pager log --pretty=oneline")))

(defn tagging [{:keys [ws-dir sections-dir fake-sha fake-sha2] :as opts}]
  (let [shell (fn-default-opts sh/shell {:dir ws-dir})
        opts (assoc opts :fake-sha fake-sha2)]
    (shell "git tag -f stable-lisa")
    (shell "git --no-pager log --pretty=oneline")

    (poly-infos opts "" "tagging-info-1.txt" "tagging/output/info-01.png")
    (let [first-sha (-> (shell {:out :string} "git rev-list --max-parents=0 HEAD")
                        :out
                        str/trim)]
      (shell "git tag v1.1.0" first-sha))
    (shell "git tag v1.2.0")

    (copy [(fs/file sections-dir "tagging/user-core-change.clj") (fs/file ws-dir "components/user/src/se/example/user/core.clj")])
    (poly-infos opts "since:release" "tagging-info-2.txt" "tagging/output/info-02.png")
    (poly-infos opts "since:release" "tagging-info-3.txt" "tagging/output/info-03.png")
    (poly-infos (assoc opts :fake-sha fake-sha)
                "since:previous-release" "tagging-info-4.txt" "tagging/output/info-04.png")
    (shell "git --no-pager log --pretty=oneline")))

(defn flags [{:keys [fake-sha2] :as opts}]
  (let [opts (assoc opts :fake-sha fake-sha2)]
    (poly-infos opts ":resources" "flags-info.txt" "flags/output/info.png")))

(defn testing [{:keys [ws-dir fake-sha2 sections-dir output-dir] :as opts}]
  (let [opts (assoc opts :fake-sha fake-sha2)
        poly (fn-default-opts sh/poly {:dir ws-dir})
        shell (fn-default-opts sh/shell {:dir ws-dir})]
    (copy [(fs/file sections-dir "testing/user-core.clj") (fs/file ws-dir "components/user/src/se/example/user/core.clj")])
    (poly {:out (fs/file output-dir "testing-diff.txt")} "diff")
    (poly-infos opts "" "testing-info-1.txt" "testing/output/info.png")
    (copy [(fs/file sections-dir "testing/user-interface-test.clj") (fs/file ws-dir "components/user/test/se/example/user/interface_test.clj")])
    (poly {:continue true
           :alter-out-fn test-result->output
           :out (fs/file output-dir "testing-test-failing.txt")}
          "test color-mode:none")
    (copy [(fs/file sections-dir "testing/user-interface-test2.clj") (fs/file ws-dir "components/user/test/se/example/user/interface_test.clj")])
    (poly {:alter-out-fn test-result->output
           :out (fs/file output-dir "testing-test-ok.txt")}
          "test color-mode:none")
    (poly-infos opts ":dev"                  "testing-info-2.txt"  "testing/output/info-dev.png")
    (poly-infos opts "project:dev"           "testing-info-2b.txt" "testing/output/info-project-dev.png")
    (poly-infos opts "project:cl:dev"        "testing-info-3.txt"  "testing/output/info-project-cl-dev.png")
    (poly-infos opts ""                      "testing-info-3a.txt" "testing/output/info-filter-on-bricks.png")
    (poly-infos opts "brick:cli"             "testing-info-3b.txt" "testing/output/info-brick-cli.png")
    (poly-infos opts ":no-changes"           "testing-info-3c.txt" "testing/output/info-no-changes.png")
    (poly-infos opts "brick:cli :no-changes" "testing-info-3d.txt" "testing/output/info-brick-cli-no-changes.png")
    (poly-infos opts ":no-changes brick:cli :all-bricks"
                "testing-info-3e.txt" "testing/output/info-brick-cli-no-changes-all-bricks.png")

    (fs/create-dir (fs/file ws-dir "projects/command-line/test"))
    (copy [(fs/file sections-dir "testing/deps.edn") ws-dir]
          [(fs/file sections-dir "testing/command-line-deps.edn") (fs/file ws-dir "projects/command-line/deps.edn")])
    (fs/create-dirs (fs/file ws-dir "projects/command-line/test/project/command_line"))
    (copy [(fs/file sections-dir "testing/dummy_test.clj") (fs/file ws-dir "projects/command-line/test/project/command_line")])
    (shell "git add projects/command-line/test/project/command_line/dummy_test.clj")
    (poly-infos opts ""         "testing-info-4.txt" "testing/output/info-project-dir.png")
    (poly-infos opts ":project" "testing-info-5.txt" "testing/output/info-project.png")
    (poly {:alter-out-fn test-result->output
           :out (fs/file output-dir "testing-test-project.txt")}
          (format "test :project fake-sha:%s color-mode:none" fake-sha2))
    (shell "git add --all")
    (shell "git commit -m 'Added tests'")
    (shell "git tag -f stable-lisa")
    (poly-infos opts ""                 "testing-info-6.txt"  "testing/output/info-added-tests.png")
    (poly-infos opts ":all-bricks"      "testing-info-7.txt"  "testing/output/info-all-bricks.png")
    (poly-infos opts ":all-bricks :dev" "testing-info-8.txt"  "testing/output/info-all-bricks-dev.png")
    (poly-infos opts ":all"             "testing-info-9.txt"  "testing/output/info-all.png")
    (poly-infos opts ":all :dev"        "testing-info-10.txt" "testing/output/info-all-dev.png")
    (poly {:out (fs/file output-dir "testing-info-11.txt")}
          (format "info :all :dev fake-sha:%s color-mode:none" fake-sha2))
    (poly {:alter-out-fn test-result->output
           :out (fs/file output-dir "testing-test-all-dev.txt")}
          "test :all :dev color-mode:none")
    (copy [(fs/file sections-dir "testing/command-line-test-setup.clj") (fs/file ws-dir "projects/command-line/test/project/command_line/test_setup.clj")]
          [(fs/file sections-dir "testing/command-line-config.edn") (fs/file ws-dir "projects/command-line/test/project/command_line/config.edn")])
    (poly {:alter-out-fn test-result->output
           :out (fs/file output-dir "testing-test-all.txt")}
          "test :all color-mode:none")

    (poly {:out (fs/file output-dir "testing-info-exclude-tests.txt")}
          (format "info :all :dev fake-sha:%s color-mode:none" fake-sha2))
    (poly {:alter-out-fn test-result->output
           :out (fs/file output-dir "testing-test-all-exclude-tests.txt")}
          "test :all :dev color-mode:none")))

(defn profile [{:keys [ws-dir sections-dir fake-sha2 output-dir] :as opts}]
  (let [shell (fn-default-opts sh/shell {:dir ws-dir})
        poly (fn-default-opts sh/poly {:dir ws-dir})
        opts (assoc opts :fake-sha fake-sha2)]
    (poly "create project name:user-service alias:user-s")
    (poly "create base name:user-api")
    (copy [(fs/file sections-dir "profile/user-api-deps.edn")     (fs/file ws-dir "bases/user-api/deps.edn")]
          [(fs/file sections-dir "profile/user-api-core.clj")     (fs/file ws-dir "bases/user-api/src/se/example/user_api/core.clj")]
          [(fs/file sections-dir "profile/user-api-api.clj")      (fs/file ws-dir "bases/user-api/src/se/example/user_api/api.clj")]
          [(fs/file sections-dir "profile/user-service-deps.edn") (fs/file ws-dir "projects/user-service/deps.edn")])
    (poly "create component name:user-remote interface:user")
    (copy [(fs/file sections-dir "profile/user-remote-deps.edn")      (fs/file ws-dir "components/user-remote/deps.edn")]
          [(fs/file sections-dir "profile/user-remote-core.clj")      (fs/file ws-dir "components/user-remote/src/se/example/user/core.clj")]
          [(fs/file sections-dir "profile/user-remote-interface.clj") (fs/file ws-dir "components/user-remote/src/se/example/user/interface.clj")]
          [(fs/file sections-dir "profile/deps.edn")                  ws-dir]
          [(fs/file sections-dir "profile/command-line-deps.edn")     (fs/file ws-dir "projects/command-line/deps.edn")])

    (shell "clojure -T:build uberjar :project command-line")
    (shell "clojure -T:build uberjar :project user-service")

    (poly-infos opts "+"       "profile-info-1.txt"  "profile/output/info-all-aliases.png")
    (poly-infos opts ""        "profile-info-1b.txt" "profile/output/info-after-adding-profiles.png")
    (poly-infos opts "+remote" "profile-info-2.txt"  "profile/output/info-with-remote-profile.png")
    ;; only doing text version because: We don't include the error message today, so we can't use the generated image.
    (poly {:continue true
           :out (fs/file output-dir "profile-info-3.txt")}
          (format "info +default +remote fake-sha:%s color-mode:none" fake-sha2))
    (poly-infos opts ":loc" "profile-info-4.txt" "profile/output/info-loc.png")
    (poly {:alter-out-fn test-result->output
           :out (fs/file output-dir "profile-test.txt")}
          "test :project color-mode:none")))

(defn configuration [{:keys [ws-dir ws-parent-dir output-dir]}]
  (let [shell (fn-default-opts sh/shell {:dir ws-dir})
        poly (fn-default-opts sh/poly {:dir ws-dir})
        sha (-> (shell {:out :string}
                       "git rev-list -n 1 stable-lisa")
                :out
                str/trim)]
    (poly {:out (fs/file output-dir "ws-state-settings.txt")}
          (format "ws get:settings replace:%s:WS-HOME:%s:SHA color-mode:none"
                  ws-parent-dir sha))
    (poly {:out (fs/file output-dir "ws-state-paths.txt")}
          "ws get:settings:profile-to-settings:default:paths color-mode:none")
    (poly {:out (fs/file output-dir "ws-state-keys.txt")}
          "ws get:keys color-mode:none")
    (poly {:out (fs/file output-dir "ws-state-components-keys.txt")}
          (format "ws get:components:keys replace:%s:WS-HOME color-mode:none"
                  ws-parent-dir))
    (poly {:out (fs/file output-dir "ws-state-components-user.txt")}
          (format "ws get:components:user replace:%s:WS-HOME color-mode:none"
                  ws-parent-dir))
    (poly {:out (fs/file output-dir "ws-state-components-user-remote-lib-deps.txt")}
          "ws get:components:user-remote:lib-deps color-mode:none")
    (poly "ws out:ws.edn")
    (poly {:out (fs/file output-dir "ws-state-ws-file.txt")}
          "ws get:old:user-input:args ws-file:ws.edn color-mode:none")))

(defn copy-doc-example [{:keys [ws-dir ws-parent-dir examples-dir]}]
  ;; Save our example workspace to ./examples/doc-example
  (copy [(fs/file examples-dir "doc-example/readme.txt") ws-parent-dir])
  (fs/delete-tree (fs/file examples-dir "doc-example"))
  (fs/copy-tree ws-dir (fs/file examples-dir "doc-example"))
  (fs/delete-tree (fs/file examples-dir "doc-example/.git"))
  (doseq [f [".gitignore" "readme.md" "logo.png" "bases/.keep" "components/.keep" "projects/.keep" "development/src/.keep"]]
    (fs/delete (fs/file examples-dir "doc-example" f)))
  (copy [(fs/file ws-parent-dir "readme.txt") (fs/file examples-dir "doc-example/readme.txt")]))

(defn real-world-example [{:keys [ws-parent-dir scripts-dir] :as opts}]
  (let [ws-dir (fs/file ws-parent-dir "clojure-polylith-realworld-example-app")
        shell (fn-default-opts sh/shell {:dir ws-dir})
        opts (assoc opts :ws-dir ws-dir)]
    (fs/create-dir ws-parent-dir)
    (shell {:dir ws-parent-dir} "git clone https://github.com/furkan3ayraktar/clojure-polylith-realworld-example-app.git")
    (shell "clojure -A:dev:test -P")
    (shell "git tag stable-lisa")

    (let [out-txt #(format "realworld/realworld-%s.txt" %)
          out-png #(format "%s/output/%s.png" %1 %2)]

      (run! (fn [[cmd section fname]]
              (polys opts cmd (out-txt fname) (out-png section fname)))
            [["info"                          "dependencies" "info"] ;; no faking the sha for this one, user will see real sha of repo, so we should show it
             ["deps"                          "dependencies" "deps-interfaces"]
             ["deps brick:article"            "dependencies" "deps-interface"]
             ["deps project:rb"               "dependencies" "deps-components"]
             ["deps project:rb :compact"      "dependencies" "deps-components-compact"]
             ["deps project:rb brick:article" "dependencies" "deps-component"]
             ["libs"                          "libraries"    "libs"]])

      ;; test out choosing compact format via workspace.edn
      (copy [(fs/file scripts-dir "realworld/workspace-compact.edn") (fs/file ws-dir "workspace.edn")])
      (let [fname "libs-compact"]
        (polys opts "libs" (out-txt fname) (out-png "libraries" fname)))
      (shell "git restore workspace.edn")
      ;; show overriding a project lib
      (let [out-fname "libs-override"
            deps-fname (str (fs/file ws-dir "projects/realworld-backend/deps.edn"))
            new-content (-> deps-fname
                            slurp
                            r/parse-string (r/assoc :override-deps {'clj-time/clj-time {:mvn/version "0.15.1"}})
                            str)]
        (spit deps-fname new-content)
        (polys opts "libs" (out-txt out-fname) (out-png "libraries" out-fname))
        (shell "git restore" deps-fname)))))

(defn polylith-toolsdeps1 [{:keys [ws-parent-dir output-dir]}]
  (let [ws-dir (fs/file ws-parent-dir "polylith")
        shell (fn-default-opts sh/shell {:dir ws-dir})
        poly (fn-default-opts sh/poly {:dir ws-dir})
        out #(fs/file output-dir "polylith1" %)]
    (fs/create-dir ws-parent-dir)
    (shell {:dir ws-parent-dir} "git clone https://github.com/polyfy/polylith.git")
    (poly {:out (out "info.txt")} "info fake-sha:40d2f62 :no-changs color-mode:none")
    (shell "clojure -A:dev:test -P")
    (poly {:out (out "libs.txt")} "libs color-mode:none")
    (poly {:out (out "deps.txt")} "deps color-mode:none")

    (status/line :head "Polylith toolsdeps1 (migrated)")
    (poly "migrate")
    (poly {:out (out "info-migrated.txt")} "info fake-sha:40d2f62 :no-changes color-mode:none")
    (poly {:out (out "libs-migrated.txt")} "libs color-mode:none")
    (poly {:out (out "deps-migrated.txt")} "deps color-mode:none")))

(defn usermanager [{:keys [ws-parent-dir output-dir]}]
  (let [ws-dir (fs/file ws-parent-dir "usermanager-example")
        shell (fn-default-opts sh/shell {:dir ws-dir})
        poly (fn-default-opts sh/poly {:dir ws-dir})
        out #(fs/file output-dir "usermanager" %)]
    (fs/create-dir ws-parent-dir)
    (shell {:dir ws-parent-dir} "git clone https://github.com/seancorfield/usermanager-example.git")
    (shell "git checkout polylith")
    (shell "clojure -A:dev:test -P")
    (poly {:out (out "info.txt")} "info :no-chanes color-mode:none")
    (poly {:out (out "libs.txt")} "libs color-mode:none")
    (poly {:out (out "deps.txt")} "deps color-mode:none")))

(defn example-localdep [{:keys [ws-parent-dir examples-dir output-dir]}]
  (let [ws-dir (fs/file examples-dir "local-dep")
        shell (fn-default-opts sh/shell {:dir ws-dir})
        poly (fn-default-opts sh/poly {:dir ws-dir})
        sha (-> (shell {:out :string} "git rev-list -n 1 stable-master")
                :out str/trim)
        branch (-> (shell {:out :string} "git rev-parse --abbrev-ref HEAD")
                   :out str/trim)
        out #(fs/file output-dir "local-dep" %)]
    (fs/create-dir ws-parent-dir)
    (poly {:out (out "info.txt")}                 "info color-mode:none fake-sha:aaaaa :no-changes")
    (poly {:out (out "libs.txt")}                 "libs color-mode:none")
    (poly {:out (out "libs-compact.txt")}         "libs :compact color-mode:none")
    (poly {:out (out "deps.txt")}                 "deps color-mode:none ")
    (poly {:out (out "deps-compact.txt")}         "deps :compact color-mode:none")
    (poly {:out (out "deps-project-compact.txt")} "deps :compact project:inv color-mode:none")
    (poly {:out (out "diff.txt")}                 "diff since:0aaeb58 color-mode:none")
    (poly (format "ws out:%s replace:%s:WS-HOME:%s:USER-HOME:%s:SHA:%s:BRANCH color-mode:none"
                  (out "ws.edn") ws-parent-dir (System/getProperty "user.home") sha branch))
    (poly {:out (out "since-info.txt")} "info :dev since:0aaeb58 color-mode:none")
    (poly {:alter-out-fn test-result->output
           :out (out "test.txt")}
          "test :dev since:0aaeb58 color-mode:none")

    (let [ws-dir (fs/file ws-parent-dir "local-dep-old-format")
          shell (fn-default-opts shell {:dir ws-dir})]
      (fs/create-dir ws-dir)
      (fs/copy-tree (fs/file examples-dir "local-dep-old-format") ws-dir)
      (shell "git init")
      (shell "git add .")
      (shell "git commit -m 'Workspace created.'")
      (shell "git tag stable-jote")
      (let [sha (-> {:out :string}
                    (shell "git rev-list -n 1 stable-jote")
                    :out str/trim)
            poly (fn-default-opts poly {:dir ws-dir})
            out #(fs/file output-dir "local-dep-old-format" %)]
        (status/line :head "examples/local-dep-old-format")
        (poly {:out (out "info.txt")} "info fake-sha:aaaaa color-mode:none :no-changes")
        (poly {:out (out "libs.txt")} "libs color-mode:none")
        (poly {:out (out "deps.txt")} "deps color-mode:none")
        (poly (format "ws out:%s replace:%s:WS-HOME:%s:USER-HOME:%s:SHA color-mode:none"
                      (out "ws.edn") ws-parent-dir (System/getProperty "user.home") sha))
        (poly {:alter-out-fn test-result->output
               :out (out "test.txt")}
              "test :dev color-mode:none")

        (status/line :head "examples/local-dep-old-format (migrated)")
        (poly "migrate")
        (shell "git add --all")
        (poly {:out (out "info-migrated.txt")} "info fake-sha:aaaaa :no-changes color-mode:none")
        (poly {:out (out "libs-migrated.txt")} "libs color-mode:none")
        (poly {:out (out "deps-migrated.txt")} "deps color-mode:none")
        (poly {:alter-out-fn test-result->output
               :out (out "test-migrated.txt")}
              "test color-mode:none :all")))))

(defn for-test [{:keys [examples-dir output-dir]}]
  (let [ws-dir (fs/file examples-dir "for-test")
        poly (fn-default-opts sh/poly {:dir ws-dir :continue true :alter-out-fn test-result->output})
        out #(fs/file output-dir "for-test" %)]
    (poly {:out (out "mix-clj-and-cljc.txt")}
          "test :all project:okay color-mode:none")
    (poly {:out (out "setup-fails-stops-entire-test-run.txt")}
          "test :all project:okay:setup-fails:x-okay color-mode:none")
    (poly {:out (out "teardown-fails-stops-entire-test-run.txt")}
          "test :all project:okay:teardown-fails:x-okay color-mode:none")
    (poly {:out (out "failing-test-runs-teardown-and-stops-entire-test-run.txt")}
          "test :all project:failing-test:okay color-mode:none")
    (poly {:out (out "failing-test-and-teardown-fails-stops-entire-test-run.txt")}
          "test :all project:failing-test-teardown-fails:okay color-mode:none")))

(defn -main [& args]
  (let [ids (mapv #(if (keyword? %) % (keyword %)) args) ;; ids to run, all if non specified, handy for dev tests
        start-time-ms (System/currentTimeMillis)
        root-dir (System/getProperty "user.dir")
        scripts-dir (fs/absolutize "scripts")
        timestamp (.format (java.time.LocalDateTime/now)
                           (java.time.format.DateTimeFormatter/ofPattern "yyyy-MM-dd-HHmmss"))
        work-dir (fs/create-dirs (fs/file (fs/temp-dir) (str "polylith-example-" timestamp)))
        default-opts {:scripts-dir scripts-dir
                      :output-dir (fs/file scripts-dir "output")
                      :sections-dir (fs/file scripts-dir "sections")
                      :examples-dir (fs/file root-dir "examples")
                      :images-dir (fs/file root-dir "doc/images")}]
    (status/line :detail "Work dir: %s" work-dir)
    (assert-tools-installed)
    (assert-min-tree-version)
    (assert-clojure-is-latest)

    (let [ws-parent-dir (fs/file work-dir "ws")
          opts (merge default-opts
                      {:ws-parent-dir ws-parent-dir
                       :ws-dir (fs/file ws-parent-dir "example")
                       :fake-sha "c91fdad"
                       :fake-sha2 "e7ebe68"})
          ;; structure as tasks to allow x of n progress in headings
          task-groups [[:gen-nav [["Generate doc navigation, used by the shell" #(gen-nav)]]]

                       ;; doc example tutorial tasks (next task tends to rely on work of previous)
                       [:example [["Workspace" #(workspace opts)]
                                  ["Development" #(development opts)]
                                  ["Component" #(component opts)]
                                  ["Base" #(base opts)]
                                  ["Project" #(project opts)]
                                  ["Polyx" #(polyx opts)]
                                  ["Tools.deps" #(tools-deps opts)]
                                  ["Build" #(build opts)]
                                  ["Git" #(git opts)]
                                  ["Tagging" #(tagging opts)]
                                  ["Flags" #(flags opts)]
                                  ["Testing" #(testing opts)]
                                  ["Profile" #(profile opts)]
                                  ["Configuration" #(configuration opts)]
                                  ["Copy doc-example" #(copy-doc-example opts)]]]

                       ;; Stand-alone tasks (can run independently)
                       [:realworld   [["Realworld example app" #(real-world-example (merge default-opts {:ws-parent-dir (fs/file work-dir "ws2")}))]]]
                       [:poly        [["Polylith toolsdeps1" #(polylith-toolsdeps1 (merge default-opts {:ws-parent-dir (fs/file work-dir "ws1")}))]]]
                       [:usermanager [["Usermanager" #(usermanager (merge default-opts {:ws-parent-dir (fs/file work-dir "ws3")}))]]]
                       [:local-dep   [["examples/local-dep" #(example-localdep (merge default-opts {:ws-parent-dir (fs/file work-dir "ws4")}))]]]
                       [:for-test    [["examples/for-test, issue 208 - Mix clj and cljc source directories" #(for-test default-opts)]]]]

          valid-ids (mapv first task-groups)

          invalid-ids (into [] (remove #(some #{%} valid-ids) ids))
          _ (when (seq invalid-ids)
              (status/die 1 "Invalid task group ids: %s\nValid ids are: %s"
                          (str/join ", " (mapv name invalid-ids))
                          (str/join ", " (mapv name valid-ids))))

          selected-task-groups (if (seq ids)
                                 (filterv (fn [[id]] (some #{id} ids)) task-groups)
                                 task-groups)
          ;; flatten for running
          selected-tasks (for [[id task-group] selected-task-groups
                               task task-group]
                           (cons id task))

          task-count (count selected-tasks)]
      (download-deps)
      (doseq [[ndx [group-id title task-fn]] (map-indexed vector selected-tasks)]
        (status/line :head (format "%d/%d [%s] %s" (inc ndx) task-count (name group-id) title))
        (task-fn)))

    (let [elapsed-secs (long (/ (- (System/currentTimeMillis) start-time-ms)
                                1000))]
      (status/line :detail "Elapsed: %d min %d sec" (long (/ elapsed-secs 60)) (mod elapsed-secs 60)))))

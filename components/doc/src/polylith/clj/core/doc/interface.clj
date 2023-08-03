(ns polylith.clj.core.doc.interface
  (:require [polylith.clj.core.doc.core :as core]))

(def command-pages {"check" [],
                    "create" [],
                    "create-base" [],
                    "create-component" [],
                    "create-project" [],
                    "create-workspace" [],
                    "deps" [],
                    "deps-brick" [],
                    "deps-project" [],
                    "deps-project-brick" [],
                    "deps-workspace" [],
                    "diff" [],
                    "info" [],
                    "libs" [],
                    "migrate" [],
                    "overview" [],
                    "shell" [],
                    "switch-ws" [],
                    "tap" [],
                    "test" [],
                    "ws" []})

;; Remember to also update other->url in core.
(def more-urls {core/realworld []
                core/slack []
                core/python-tool []
                core/high-level []
                core/blog-post {core/the-monorepos-polylith-series []
                                core/a-fresh-take-on-monorepos-in-python-blog-post []}
                core/podcast {core/polylith-with-joakim-james-and-furkan {core/part1 []
                                                                          core/part2 []}}
                core/video {core/a-fresh-take-on-monorepos-video []
                            core/polylith-in-a-nutshell []
                            core/the-last-architecture-you-will-ever-need []
                            core/los-angeles-clojure-users-group []
                            core/polylith–a-software-architecture-based-on-lego-like-blocks []}})

(def pages {"base" {},
            "build" {},
            "clojure-cli-tool" {},
            "colors" {},
            "component" {},
            "configuration" {},
            "context" {},
            "continuous-integration" {},
            "dependencies" {},
            "development" {},
            "example-systems" {},
            "explore-the-workspace" {},
            "flags" {},
            "git" {},
            "git-hooks" {},
            "install" {},
            "interface" {},
            "introduction" {},
            "libraries" {},
            "migrate" {},
            "naming" {},
            "parameters" {},
            "polyx" {},
            "profile" {},
            "project" {},
            "readme" {},
            "shell" {},
            "source-code" {},
            "tagging" {},
            "tap" {},
            "test-runners" {},
            "testing" {},
            "tools-deps" {},
            "upgrade" {},
            "validations" {},
            "workspace" {}})

(def ws-pages {"bases" [],
               "changes" [],
               "components" [],
               "configs" [],
               "interface" [],
               "messages" [],
               "name" [],
               "old" [],
               "paths" [],
               "projects" [],
               "settings" [],
               "user-input" [],
               "version" [],
               "ws-dir" [],
               "ws-local-dir" [],
               "ws-reader" [],
               "ws-type" []})

(defn open-doc [cmd command more page ws]
  (core/open-doc cmd command more page ws))

(ns polylith.clj.core.help.summary
  (:require [polylith.clj.core.help.shared :as s]
            [polylith.clj.core.system.interface :as system]
            [polylith.clj.core.version.interface :as version]
            [polylith.clj.core.util.interface.color :as color]))

(defn migrate [toolsdeps1?]
  (if toolsdeps1?
    "    migrate                     Migrates the workspace to the latest format.\n"
    ""))

(defn migrate-command [show-migrate?]
  (if show-migrate?
    "    poly migrate\n"
    ""))

(defn help-text [show-migrate? cm]
  (str
    "  Poly " version/name " (" version/date ") - " (color/blue cm "https://github.com/polyfy/polylith\n")
    "\n"
    "  poly " (s/key "CMD" cm) " [" (s/key "ARGS" cm) "] - where " (s/key "CMD" cm) " [" (s/key "ARGS" cm) "] are:\n"
    "\n"
    "    check [" (s/key "ARG" cm) "]                 Checks if the workspace is valid.\n"
    "    create " (s/key "E" cm) " name:" (s/key "N" cm) " [" (s/key "ARG" cm) "]       Creates a component, base, project or workspace.\n"
    "    deps [project:" (s/key "P" cm) "] [brick:" (s/key "B" cm) "]  Shows dependencies.\n"
    "    diff [" (s/key "ARG" cm) "]                  Shows changed files since last stable point in time.\n"
    "    help [" (s/key "C" cm) "] [" (s/key "ARG" cm) "]              Shows this help or help for specified command.\n"
    "    info [" (s/key "ARGS" cm) "]                 Shows a workspace overview and checks if it's valid.\n"
    "    libs [" (s/key "ARGS" cm) "]                 Shows all libraries in the workspace.\n"
    (migrate show-migrate?)
    "    shell [" (s/key "ARGS" cm) "]                Starts an interactive shell.\n"
    "    test [" (s/key "ARGS" cm) "]                 Runs tests.\n"
    "    version                     Shows current version of the tool.\n"
    "    ws [get:" (s/key "X" cm) "]                  Shows the workspace as data.\n"
    "\n"
    "  From the shell:\n"
    "\n"
    "    switch-ws " (s/key "ARG" cm) "  Switches to specified workspace.\n"
    "    tap [" (s/key "ARG" cm) "]      Opens a portal window that outputs " (s/key "tap>" cm) " statements.\n"
    "    exit           Exits the shell.\n"
    "    quit           Quits the shell.\n"
    "\n"
    "  The " (s/key "ws-dir" cm) " and " (s/key "ws-file" cm) " parameters are replaced by " (s/key "switch-ws" cm) " when executing commands\n"
    "  from the shell.\n"
    "\n"
    "  If " (s/key "ws-dir:PATH" cm) " is passed in as an argument, where " (s/key "PATH" cm) " is a relative\n"
    "  or absolute path, then the command is executed from that directory.\n"
    "  This works for all commands except 'test'.\n"
    "  If the 'switch-ws dir:" (s/key "DIR" cm) "' command has been executed from a shell,\n"
    "  then ws-dir:" (s/key "DIR" cm) " will automatically be appended to following commands.\n"
    "\n"
    "  If " (s/key "ws-file:FILE" cm) " is passed in, then the workspace will be populated with the content\n"
    "  from that file. All commands except 'create' and 'test' can be executed with this\n"
    "  parameter set. The " (s/key "FILE" cm) " is created by executing the 'ws' command, e.g.:\n"
    "  'poly ws out:ws.edn'.\n"
    "  If the 'switch-ws file:" (s/key "FILE" cm) "' command has been executed from a shell,\n"
    "  then ws-file:" (s/key "FILE" cm) " will automatically be appended to following commands.\n"
    "\n"
    "  If " (s/key "::" cm) " is passed in, then ws-dir is set to the first parent directory (or current)\n"
    "  that contains a 'workspace.edn' config file. The exception is the 'test' command\n"
    "  that has to be executed from the workspace root.\n"
    "\n"
    "  If " (s/key "skip:PROJECTS" cm) " is passed in, then the given project(s) will not be read from disk.\n"
    "  Both project names and aliases can be used and should be separated by : if more than one.\n"
    "\n"
    "  If " (s/key "since:SINCE" cm) " is passed in as an argument, the last stable point in time will be\n"
    "  used depending on the value of " (s/key "SINCE" cm) " (or the first commit if no match was found).\n"
    "  If prefixed with 'previous-', e.g. 'previous-release', then the SHA directly before\n"
    "  the most recent matching tag of the 'release' pattern will be used:\n"
    "    stable  -> the latest tag that matches stable-*, defined by\n"
    "               " (s/key ":tag-patterns > :stable" cm) " in workspace.edn.\n"
    "    release -> the latest tag that matches v[0-9]*, defined by\n"
    "               " (s/key ":tag-patterns > :release" cm) " in workspace.edn.\n"
    "    KEY     -> any key in " (s/key ":tag-patterns" cm) ".\n"
    "    SHA     -> a git SHA-1 hash (if no key was found in " (s/key ":tag-patterns" cm) ").\n"
    "\n"
    "  The color mode is taken from ~/.config/polylith/config.edn but can be overridden by passing\n"
    "  in " (s/key "color-mode:COLOR" cm) " where valid colors are " (s/key "none" cm) ", " (s/key "light" cm) ", and " (s/key "dark" cm) ".\n"
    "  (if the XDG_CONFIG_HOME environment variable is set, that will be used instead of ~/.config)\n"
    "\n"
    "  Example (shell only):\n"
    "    switch-ws dir:~/myworkspace\n"
    "    switch-ws file:../../another/ws.edn\n"
    "    tap\n"
    "    tap open\n"
    "    tap clean\n"
    "    tap close\n"
    "\n"
    "  Example:\n"
    "    poly\n"
    "    poly check\n"
    "    poly check :dev\n"
    "    poly create c name:user\n"
    "    poly create component name:user\n"
    "    poly create component name:admin interface:user\n"
    "    poly create b name:mybase\n"
    "    poly create base name:mybase\n"
    "    poly create project name:myproject\n"
    "    poly create w top-ns:com.my.company\n"
    "    poly create workspace name:myws top-ns:com.my.company\n"
    "    poly create workspace name:myws top-ns:com.my.company branch:master\n"
    "    poly deps\n"
    "    poly deps brick:mybrick\n"
    "    poly deps project:myproject\n"
    "    poly deps project:myproject brick:mybrick\n"
    "    poly deps out:deps.txt\n"
    (if system/admin-tool?
      "    poly deps out:deps.png\n" "")
    "    poly diff\n"
    "    poly help\n"
    "    poly help info\n"
    "    poly help create\n"
    "    poly help create component\n"
    "    poly help create base\n"
    "    poly help create project\n"
    "    poly help create workspace\n"
    "    poly help deps\n"
    "    poly help deps :project\n"
    "    poly help deps :brick\n"
    "    poly help deps :project :brick\n"
    "    poly help deps :workspace\n"
    "    poly info\n"
    "    poly info :loc\n"
    "    poly info since:65a7918\n"
    "    poly info since:head\n"
    "    poly info since:head~1\n"
    "    poly info since:stable\n"
    "    poly info since:release\n"
    "    poly info since:previous-release\n"
    "    poly info skip:dev\n"
    "    poly info skip:dev:myproject\n"
    "    poly info project:myproject\n"
    "    poly info project:myproject:another-project\n"
    "    poly info brick:mycomponent\n"
    "    poly info brick:mycomponent:mybase\n"
    "    poly info color-mode:none\n"
    "    poly info :project\n"
    "    poly info :dev\n"
    "    poly info :project :dev\n"
    "    poly info :all\n"
    "    poly info :all-bricks\n"
    "    poly info ::\n"
    "    poly info out:info.txt\n"
    (if system/admin-tool?
      "    poly info out:info.png\n" "")
    "    poly info ws-dir:another-ws\n"
    "    poly info ws-file:ws.edn\n"
    "    poly libs\n"
    "    poly libs :compact\n"
    "    poly libs :outdated\n"
    "    poly libs out:libs.txt\n"
    (if system/admin-tool?
      "    poly libs out:libs.png\n" "")
    (migrate-command show-migrate?)
    "    poly shell\n"
    "    poly shell :tap\n"
    "    poly shell :all\n"
    "    poly test\n"
    "    poly test :project\n"
    "    poly test :all-bricks\n"
    "    poly test :all\n"
    "    poly test project:proj1\n"
    "    poly test project:proj1:proj2\n"
    "    poly test brick:mycomponent\n"
    "    poly test brick:mycomponent:mybase\n"
    "    poly test :dev\n"
    "    poly test :project :dev\n"
    "    poly test :all-bricks :dev\n"
    "    poly test :all :dev\n"
    "    poly version\n"
    "    poly ws\n"
    "    poly ws get:keys\n"
    "    poly ws get:count\n"
    "    poly ws get:configs\n"
    "    poly ws get:settings\n"
    "    poly ws get:user-input:args\n"
    "    poly ws get:user-input:args:0\n"
    "    poly ws get:settings:keys\n"
    "    poly ws get:components:keys\n"
    "    poly ws get:components:count\n"
    "    poly ws get:components:mycomp:lines-of-code\n"
    "    poly ws get:settings:vcs:polylith :latest-sha\n"
    "    poly ws get:settings:vcs:polylith :latest-sha branch:master\n"
    "    poly ws get:changes:changed-or-affected-projects skip:dev color-mode:none\n"
    "    poly ws out:ws.edn"))

(defn print-help [is-all toolsdeps1? color-mode]
  (let [show-migrate? (or is-all toolsdeps1?)]
    (println (help-text show-migrate? color-mode))))

(comment
  (print-help false false "dark")
  #__)

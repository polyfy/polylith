(ns polylith.clj.core.help.diff
  (:require [polylith.clj.core.help.shared :as s]))

(defn help [cm]
  (str "  Shows changed files since the most recent stable point in time.\n"
       "\n"
       "  poly diff [" (s/key "ARG" cm) "]\n"
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
       "  Internally, it executes 'git diff SHA --name-only' where SHA is the SHA-1\n"
       "  of the first commit in the repository, or the SHA-1 of the most recent tag\n"
       "  that matches the default pattern 'stable-*' or the passed in since:SINCE.\n"
       "\n"
       "  Stable points are normally set by the CI server or by individual developers,\n"
       "  e.g. Lisa, with 'git tag -f stable-lisa'.\n"
       "\n"
       "  The pattern can be changed in " (s/key ":tag-patterns" cm) " in workspace.edn.\n"
       "\n"
       "  The way the latest tag is found is by taking the first line that matches the 'stable-*'\n"
       "  regular expression, or if no match was found, the first commit in the repository.\n"
       "    git log --pretty=format:'%H %d'\n"
       "\n"
       "  Here is a compact way of listing all the commits including tags:\n"
       "    git log --pretty=oneline"))

(defn print-help [color-mode]
  (println (help color-mode)))

(comment
  (print-help "dark")
  #__)

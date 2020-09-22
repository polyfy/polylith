(ns polylith.clj.core.help.diff
  (:require [polylith.clj.core.util.interface.color :as color]))

(defn help [color-mode]
  (str "  Shows changed files since the most recent stable point in time.\n"
       "\n"
       "  poly diff\n"
       "\n"
       "  Internally, it executes 'git diff SHA --name-only' where SHA is the SHA-1\n"
       "  of the first commit in the repository, or the SHA-1 of the most recent tag\n"
       "  that matches the default pattern 'stable-*'.\n"
       "\n"
       "  Stable points are normally set by the CI server or by individual developers,\n"
       "  e.g. Lisa, with 'git tag -f stable-lisa'.\n"
       "\n"
       "  The pattern can be changed in " (color/purple color-mode ":stable-since-tag-pattern") " in ./deps.edn.\n"
       "\n"
       "  The way the latest tag is found is by taking the last line of the output from:\n"
       "    git tag --sort=committerdate -l 'stable-*'\n"
       "\n"
       "  Here is a compact way of listing all the commits including tags:\n"
       "    git log --pretty=oneline"))

(defn print-help [color-mode]
  (println (help color-mode)))

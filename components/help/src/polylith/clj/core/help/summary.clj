(ns polylith.clj.core.help.summary
  (:require [polylith.clj.core.util.interfc.color :as color]))

(defn env [env color-mode]
  (color/environment env color-mode))

(defn help-text [color-mode]
  (str
    "  Polylith - https://github.com/tengstrand/polylith/tree/core\n\n"
    "  poly CMD [ARGS]  - where CMD [ARGS] are:\n\n"
    "    check        Checks that the workspace is valid.\n"
    "    help [C]     Shows this help or help for a specified command.\n"
    "    info [ARG]   Shows information about the workspace and checks that it's valid.\n"
    "    test [" (env "env" color-mode) "]   Runs the tests for the given environment, or all if not given.\n"
    "\n"
    "  Examples:\n"
    "    poly\n"
    "    poly check\n"
    "    poly help\n"
    "    poly help info\n"
    "    poly info\n"
    "    poly info -loc\n"
    "    poly info -dump\n"
    "    poly test\n"
    "    poly test dev"))

(defn print-help [color-mode]
  (-> color-mode help-text println))

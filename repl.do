#!/usr/bin/env bash

# clojure -M:repl/cider
# clojure -M:repl/rebl

xterm \
  -bg wheat \
  -fg black \
  -fa 'Fantasque Sans Mono' \
  -fs 12 \
  -sb \
  -hold -e "source /usr/share/usepackage/use.bsh ; use rkclj ; clojure -M:dev:repl/rebl" 1>&2

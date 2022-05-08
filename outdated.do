#!/usr/bin/env bash

source /usr/share/usepackage/use.bsh

use rkclj

# clojure -T:project/outdated :upgrade true 1>&2
clojure -T:project/outdated  1>&2

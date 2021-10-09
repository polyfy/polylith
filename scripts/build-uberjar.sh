#!/usr/bin/env bash
set -e

if [[ $# -ne 1 ]]
then
    echo "Usage: PROJECT_NAME, e.g.: dev"
    exit 1
fi

clojure -A:deps -T:build uberjar :project $1

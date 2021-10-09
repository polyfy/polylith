#!/usr/bin/env bash
set -e

if [[ $# -ne 1 ]]
then
    echo "Usage: PROJECT_NAME, e.g.: dev"
    exit 1
fi

clojure -A:deps -T:build jar :project $1

if [[ $? -ne 0 ]]
then
  echo "Could not create thin jar for the project."
  exit 1
fi

echo "Thin jar created."

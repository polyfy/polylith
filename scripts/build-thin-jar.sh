#!/usr/bin/env bash
set -e

if [[ $# -ne 1 ]]
then
    echo "Usage: PROJECT_NAME, e.g.: dev"
    exit 1
fi

cd ../projects/$1

mkdir -p target

rm -rf target/$1.*

clojure -X:jar

if [[ $? -ne 0 ]]
then
  echo "Could not create thin jar for the project."
  exit 1
fi

echo "Thin jar created."

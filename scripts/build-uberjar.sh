#!/usr/bin/env bash
set -e

if [[ $# -ne 1 ]]
then
    echo "Usage: PROJECT_NAME, e.g.: dev"
    exit 1
fi

cd ../projects/$1

echo "Creating an AOT compiled uberjar for the project"

clojure -X:uberjar :aot true :jar target/$1.jar

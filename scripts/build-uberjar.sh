#!/usr/bin/env bash
set -e

if [[ $# -ne 1 ]]
then
    echo "Usage: PROJECT_NAME, e.g.: dev"
    exit 1
fi

cd ../projects/$1

mkdir -p classes
mkdir -p target

rm -rf classes/*
rm -rf target/$1.*

echo "Compiling project"

clojure -A:aot

if [[ $? -ne 0 ]]
then
  echo "Could not compile project."
  exit 1
fi

echo "Project compiled. Creating an uberjar for the project"

clojure -A:uberjar

if [[ $? -ne 0 ]]
then
  echo "Could not create uberjar for the project."
  exit 1
fi

echo "Uberjar created."

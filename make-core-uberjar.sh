#!/usr/bin/env bash
set -e

cd environments/core

mkdir -p classes
mkdir -p target

rm -rf classes/*
rm -rf target/core.*

echo "Compiling environment"

clojure -A:aot

if [[ $? -ne 0 ]]
then
  echo "Could not compile environment."
  exit 1
fi

echo "Environment compiled. Creating an uberjar for core"

clojure -A:uberjar --aliases aot --target ./target/core.jar

if [[ $? -ne 0 ]]
then
  echo "Could not create uberjar for core."
  exit 1
fi

echo "Uberjar created."

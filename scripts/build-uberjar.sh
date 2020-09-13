#!/usr/bin/env bash
set -e

if [[ $# -ne 1 ]]
then
    echo "Usage: ENV_NAME, e.g.: dev"
    exit 1
fi

cd ../environments/$1

mkdir -p classes
mkdir -p target

rm -rf classes/*
rm -rf target/$1.*

echo "Compiling environment"

clojure -A:aot

if [[ $? -ne 0 ]]
then
  echo "Could not compile environment."
  exit 1
fi

echo "Environment compiled. Creating an uberjar for the environment"

clojure -A:uberjar

if [[ $? -ne 0 ]]
then
  echo "Could not create uberjar for the environment."
  exit 1
fi

echo "Uberjar created."

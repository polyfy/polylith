#!/usr/bin/env bash

echo "#### Build poly uberjar"
cd scripts
./build-uberjar.sh poly polylith.clj.core.poly-cli.core
sudo cp ../environments/poly/target/poly.jar /usr/local/polylith/poly.jar

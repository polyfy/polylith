#!/usr/bin/env bash

echo "#### Build poly uberjar"
cd scripts
./build-uberjar.sh poly
sudo cp ../environments/poly/target/poly.jar /usr/local/polylith/poly.jar

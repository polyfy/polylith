#!/usr/bin/env bash

echo "#### Build migrator uberjar"
./build-uberjar.sh migrator
sudo cp ../environments/migrator/target/migrator.jar /usr/local/polylith/poly-migrator.jar

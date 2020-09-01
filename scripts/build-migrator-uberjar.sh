#!/usr/bin/env bash

echo "#### Build migrator uberjar"
./build-uberjar.sh migrator polylith.clj.core.migrator-cli.migrate
sudo cp ../environments/migrator/target/migrator.jar /usr/local/polylith/poly-migr.jar

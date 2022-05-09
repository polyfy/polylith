#!/usr/bin/env bash

echo "#### Build poly uberjar"
clojure -T:build uberjar :project poly
#sudo cp projects/poly/target/poly.jar /usr/local/polylith/poly.jar

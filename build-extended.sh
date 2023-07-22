#!/usr/bin/env bash

echo "#### Build poly(x) uberjar"
clojure -T:build uberjar :project polyx
sudo cp projects/polyx/target/polyx.jar /usr/local/polylith/poly.jar

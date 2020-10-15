#!/usr/bin/env bash

echo "#### Build Clojure core uberjar"
./build-uberjar.sh core
sudo cp ../projects/core/target/core.jar ~/.m2/repository/polylith/polylith-clj-core/1.0/polylith-clj-core-1.0.jar

echo "#### Build Java core API"
cd ../java
mvn clean package
sudo cp target/polylith-java-core-jar-with-dependencies.jar ~/.m2/repository/polylith/polylith-java-core/0.1.0/polylith-java-core-0.1.0.jar

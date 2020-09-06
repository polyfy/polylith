#!/usr/bin/env bash

echo "#### Build core API"
cd ../java
mvn clean package
sudo cp target/polylith-java-core-jar-with-dependencies.jar ~/.m2/repository/polylith/polylith-java-core/0.1.0/polylith-java-core-0.1.0.jar

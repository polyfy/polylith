
rm ./environments/cli/target/dev.jar
rm ./environments/core/target/core.jar

echo "#### 1. Build core uberjar"

./make-core-uberjar.sh
sudo cp ./environments/core/target/core.jar /Users/tengstrand/.m2/repository/polylith/polylith-core/0.1.0/polylith-core-0.1.0.jar

echo "#### 2. Build cli uberjar"

./make-uberjar.sh cli polylith.clj.core.cli.poly
sudo cp ./environments/cli/target/cli.jar /usr/local/polylith/poly.jar

#echo "#### 3. Build core API"

#cd java

#mvn clean package
#sudo cp target/polylith-core-jar-with-dependencies.jar /Users/tengstrand/.m2/repository/polylith/polylith-core/0.1.0/polylith-core-0.1.0.jar

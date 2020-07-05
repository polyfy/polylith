
rm ./environments/dev/target/dev.jar
rm ./environments/core/target/core.jar

echo "#### 1. Make core uberjar"

./make-core-uberjar.sh

echo "#### 2. Make poly tool uberjar"

./make-uberjar.sh dev polylith.clj.cli_tool.cli.poly

echo "#### 3. Copy core + poly tool"
sudo cp ./environments/dev/target/dev.jar /usr/local/polylith/poly.jar
sudo cp ./environments/core/target/core.jar /Users/tengstrand/.m2/repository/polylith/polylith-core/0.1.0/polylith-core-0.1.0.jar

echo "#### 4. Build core API"

cd java

mvn clean package

echo "#### 5. Copy core API"

sudo cp target/polylith-core-jar-with-dependencies.jar /Users/tengstrand/.m2/repository/polylith/polylith-core/0.1.0/polylith-core-0.1.0.jar

cd ..

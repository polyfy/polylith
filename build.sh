
#echo "#### Build core uberjar"
#./make-core-uberjar.sh
#sudo cp ./environments/core/target/core.jar /Users/tengstrand/.m2/repository/polylith/polylith-clj-core/1.0/polylith-clj-core-1.0.jar

#echo "#### Build migrator uberjar"
#./make-uberjar.sh migrator polylith.clj.core.migrator_cli.migrate
#sudo cp ./environments/migrator/target/migrator.jar /usr/local/polylith/poly-migr.jar

echo "#### Build cli uberjar"
./make-uberjar.sh poly polylith.clj.core.poly_cli.poly
sudo cp ./environments/poly/target/poly.jar /usr/local/polylith/poly.jar

#echo "#### Build core API"
#cd java
#mvn clean package
#sudo cp target/polylith-java-core-jar-with-dependencies.jar /Users/tengstrand/.m2/repository/polylith/polylith-java-core/0.1.0/polylith-java-core-0.1.0.jar


This is the example in the documentation: https://github.com/polyfy/polylith

According to the documentation:

cd scripts
chmod +x build-user-service-uberjar.sh
./build-user-service-uberjar.sh

cd ../projects/user-service/target
java -jar user-service.jar

It should output:
server started: http://127.0.0.1:2104

In a new shell:
cd ../../command-line/target
java -jar command-line.jar Lisa

It should output:
Hello Lisa - from the server!!

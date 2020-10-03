#!/usr/bin/env bash

# brew install tree

SECONDS=0

set -e
rm -rf example
rm -rf scripts

echo "### Workspace ###"
poly create w name:example top-ns:se.example
tree example > sections/workspace/tree.txt
cp example/deps.edn sections/workspace
cd example
poly info > ../sections/workspace/info.txt

echo "### Development ###"
mkdir development/src/dev
cp ../sections/development/lisa.clj development/src/dev
git add development/src/dev/lisa.clj

echo "### Component ###"
poly create c name:user
cp ../sections/component/user-core.clj components/user/src/se/example/user/core.clj
git add components/user/src/se/example/user/core.clj
cp ../sections/component/user-interface.clj components/user/src/se/example/user/interface.clj
cp ../sections/component/deps.edn .

poly info > ../sections/component/info.txt
cd ..
tree example > sections/component/tree.txt
cd example

echo "### Base ###"
poly create b name:cli
cd ..
tree example > sections/base/tree.txt
cd example
cp ../sections/base/deps.edn .
cp ../sections/base/cli-core.clj bases/cli/src/se/example/cli/core.clj

echo "### Environment ###"
poly create e name:command-line
cd ..
tree example > sections/environment/tree.txt
cd example
cp ../sections/environment/deps.edn .
cp ../sections/environment/command-line-deps.edn environments/command-line/deps.edn

echo "### Tools.deps ###"
cd environments/command-line
mkdir -p classes
clj -e "(compile,'se.example.cli.core)"

echo "### Build ###"
cd ../..
mkdir scripts
cp ../../scripts/build-uberjar.sh scripts
cp ../sections/build/build-cli-uberjar.sh scripts
chmod +x scripts/build-uberjar.sh
chmod +x scripts/build-cli-uberjar.sh
git add scripts/build-uberjar.sh
git add scripts/build-cli-uberjar.sh
cp ../sections/build/command-line-deps.edn environments/command-line/deps.edn
cd scripts
./build-cli-uberjar.sh
cd ../environments/command-line/target
java -jar command-line.jar Lisa

echo "### Git ###"
cd ../../..
poly info > ../sections/git/info.txt
git log > ../sections/git/log.txt
poly diff > ../sections/git/diff.txt
git add --all
git commit -m "Created the user and cli bricks."
git log --pretty=oneline > ../sections/git/log-pretty.txt

echo "### Tagging ###"
git tag -f stable-lisa
git log --pretty=oneline > ../sections/tagging/log.txt
poly info > ../sections/tagging/info.txt

echo "### Testing ###"
cp ../sections/testing/user-core.clj components/user/src/se/example/user/core.clj
poly diff > ../sections/testing/diff.txt
poly info > ../sections/testing/info.txt
cp ../sections/testing/user-interface-test.clj components/user/test/se/example/user/interface_test.clj
set +e
poly test > ../sections/testing/test-failing.txt
set -e
cp ../sections/testing/user-interface-test2.clj components/user/test/se/example/user/interface_test.clj
poly test > ../sections/testing/test-ok.txt
poly info :dev > ../sections/testing/info-dev.txt
poly info env:cl:dev > ../sections/testing/info-cl-dev.txt
mkdir environments/command-line/test
cp ../sections/testing/deps.edn .
cp ../sections/testing/command-line-deps.edn environments/command-line/deps.edn
mkdir environments/command-line/test/env
cp ../sections/testing/dummy_test.clj environments/command-line/test/env
git add environments/command-line/test/env/dummy_test.clj
poly info :env > ../sections/testing/info-env.txt
git add --all
git commit -m "Added tests"
git tag -f stable-lisa
poly info > ../sections/testing/info-commited.txt
poly info :all-bricks > ../sections/testing/info-all-bricks.txt
poly info :all-bricks :dev > ../sections/testing/info-all-bricks-dev.txt
poly info :all > ../sections/testing/info-all-bricks-all.txt
poly info :all :dev > ../sections/testing/info-all-bricks-all-dev.txt

echo "### Profile ###"
poly create e name:user-service
cp ../sections/profile/user-service-deps.edn environments/user-service/deps.edn
poly create b name:user-api
cp ../sections/profile/user-api-core.clj bases/user-api/src/se/example/user_api/core.clj
cp ../sections/profile/user-api-api.clj bases/user-api/src/se/example/user_api/api.clj
poly create c name:user-remote interface:user

cp ../sections/profile/user-remote-core.clj components/user-remote/src/se/example/user/core.clj
cp ../sections/profile/user-remote-interface.clj components/user-remote/src/se/example/user/interface.clj
cp ../sections/profile/deps.edn .
cp ../sections/profile/command-line-deps.edn environments/command-line/deps.edn
cd scripts
./build-cli-uberjar.sh
cd ..
cp ../sections/profile/build-user-service-uberjar.sh scripts
cd scripts
chmod +x build-user-service-uberjar.sh
./build-user-service-uberjar.sh
cd ../scripts
nohup 'java -jar service.jar' &
cd ../environments/command-line/target
#java -jar command-line.jar Lisa

echo "Elapsed: $((($SECONDS / 60) % 60)) min $(($SECONDS % 60)) sec"

#!/usr/bin/env bash

# brew install tree

SECONDS=0

set -e
example=$(pwd)
output=$(pwd)/output
sections=$(pwd)/sections
scripts=$(pwd)/../scripts
realworld=$(pwd)/../../clojure-polylith-realworld-example-app
ws=$(mktemp -d -t polylith-ws-$(date +%Y-%m-%d-%H%M%S))
ws1=$(mktemp -d -t polylith-ws1-$(date +%Y-%m-%d-%H%M%S))
echo $(pwd)
echo $ws
echo $ws1
cd $ws

echo "### 1/25 Workspace ###"
poly create w name:example top-ns:se.example :git-add
tree example > $output/workspace-tree.txt
cd example

echo "### 2/25 Development ###"
mkdir development/src/dev
cp $sections/development/lisa.clj development/src/dev
git add development/src/dev/lisa.clj

echo "### 3/25 Component ###"
poly create c name:user
tree . > ../component-tree.txt

cp $sections/component/user-core.clj components/user/src/se/example/user/core.clj
git add components/user/src/se/example/user/core.clj
cp $sections/component/user-interface.clj components/user/src/se/example/user/interface.clj
cp $sections/component/deps.edn .
poly info fake-sha:c91fdad > $output/component-info.txt

echo "### 4/25 Base ###"
poly create b name:cli
cd ..
tree example > $output/base-tree.txt
cd example
cp $sections/base/deps.edn .
cp $sections/base/cli-core.clj bases/cli/src/se/example/cli/core.clj

echo "### 5/25 Project ###"
poly create p name:command-line
cd ..
tree example > $output/project-tree.txt
cd example
cp $sections/project/deps.edn .
cp $sections/project/workspace.edn .
cp $sections/project/command-line-deps.edn projects/command-line/deps.edn

echo "### 6/25 Tools.deps ###"
cd projects/command-line
mkdir -p classes
clj -e "(compile,'se.example.cli.core)"

echo "### 7/25 Build ###"
cd ../..
mkdir scripts
cp $scripts/build-uberjar.sh scripts
cp $sections/build/build-cli-uberjar.sh scripts
chmod +x scripts/build-uberjar.sh
chmod +x scripts/build-cli-uberjar.sh
git add scripts/build-uberjar.sh
git add scripts/build-cli-uberjar.sh
cp $sections/build/command-line-deps.edn projects/command-line/deps.edn
cd scripts
./build-cli-uberjar.sh
  cd ../projects/command-line/target
java -jar command-line.jar Lisa

echo "### 8/25 Git ###"
cd ../../..
poly info fake-sha:c91fdad > $output/git-info.txt
git log
poly diff > $output/git-diff.txt
git add --all
git commit -m "Created the user and cli bricks."
git log --pretty=oneline

echo "### 9/25 Tagging ###"
git tag -f stable-lisa
git log --pretty=oneline
poly info fake-sha:e7ebe68 > $output/tagging-info-1.txt
firstsha=`git log --pretty=oneline | tail -1 | cut -d " " -f1`
git tag v1.1.0 $firstsha
git tag v1.2.0
poly info since:release fake-sha:e7ebe68 > $output/tagging-info-2.txt
poly info since:previous-release fake-sha:c91fdad > $output/tagging-info-3.txt
git log --pretty=oneline

echo "### 10/25 Flags ###"
poly info :r fake-sha:e7ebe68 > $output/flags-info.txt

echo "### 11/25 Testing ###"
cp $sections/testing/user-core.clj components/user/src/se/example/user/core.clj
poly diff > $output/testing-diff.txt
poly info fake-sha:e7ebe68 > $output/testing-info-1.txt
cp $sections/testing/user-interface-test.clj components/user/test/se/example/user/interface_test.clj
set +e
poly test > $output/testing-test-failing.txt
set -e
cp $sections/testing/user-interface-test2.clj components/user/test/se/example/user/interface_test.clj
poly test > $output/testing-test-ok.txt
poly info :dev fake-sha:e7ebe68 > $output/testing-info-2.txt
poly info project:cl:dev fake-sha:e7ebe68 > $output/testing-info-3.txt
mkdir projects/command-line/test
cp $sections/testing/deps.edn .
cp $sections/testing/command-line-deps.edn projects/command-line/deps.edn
mkdir projects/command-line/test/project
cp $sections/testing/dummy_test.clj projects/command-line/test/project
git add projects/command-line/test/project/dummy_test.clj
poly info fake-sha:e7ebe68 > $output/testing-info-4.txt
poly info :project fake-sha:e7ebe68 > $output/testing-info-5.txt
git add --all
git commit -m "Added tests"
git tag -f stable-lisa
poly info fake-sha:e7ebe68 > $output/testing-info-6.txt
poly info :all-bricks fake-sha:e7ebe68 > $output/testing-info-7.txt
poly info :all-bricks :dev fake-sha:e7ebe68 > $output/testing-info-8.txt
poly info :all fake-sha:e7ebe68 > $output/testing-info-9.txt
poly info :all :dev fake-sha:e7ebe68 > $output/testing-info-10.txt
cp $sections/testing/workspace.edn .
poly info :all :dev fake-sha:e7ebe68 > $output/testing-info-11.txt
poly test :all :dev > $output/testing-test-all.txt

echo "### 12/25 Profile ###"
cp $sections/profile/workspace.edn .
poly create p name:user-service
poly create b name:user-api
cp $sections/profile/user-api-deps.edn bases/user-api/deps.edn
cp $sections/profile/user-api-core.clj bases/user-api/src/se/example/user_api/core.clj
cp $sections/profile/user-api-api.clj bases/user-api/src/se/example/user_api/api.clj
cp $sections/profile/user-service-deps.edn projects/user-service/deps.edn
poly create c name:user-remote interface:user
cp $sections/profile/user-remote-deps.edn components/user-remote/deps.edn
cp $sections/profile/user-remote-core.clj components/user-remote/src/se/example/user/core.clj
cp $sections/profile/user-remote-interface.clj components/user-remote/src/se/example/user/interface.clj
cp $sections/profile/deps.edn .
cp $sections/profile/command-line-deps.edn projects/command-line/deps.edn
cd scripts
./build-cli-uberjar.sh
cd ..
cp $sections/profile/build-user-service-uberjar.sh scripts
cd scripts
chmod +x build-user-service-uberjar.sh
./build-user-service-uberjar.sh
#cd ../projects/user-service/target
#nohup 'java -jar service.jar' &
#cd ../../command-line/target
#java -jar command-line.jar Lisa
cd ..
poly info + fake-sha:e7ebe68 > $output/profile-info-1.txt
poly info +remote fake-sha:e7ebe68 > $output/profile-info-2.txt
set +e
poly info +default +remote fake-sha:e7ebe68 > $output/profile-info-3.txt
set -e
poly info :loc fake-sha:e7ebe68 > $output/profile-info-4.txt
poly test :project fake-sha:e7ebe68 > $output/profile-test.txt

echo "### 13/25 Configuration ###"
poly ws get:settings
poly ws get:settings:profile-to-settings:default:paths
poly ws get:keys
poly ws get:components:keys
poly ws out:ws.edn
poly info ws-file:ws.edn fake-sha:e7ebe68
poly ws get:old:user-input:args ws-file:ws.edn > $output/config-ws.txt

echo "### 14/25 Workspace state ###"
poly ws get:settings color-mode:none > $output/ws-state-settings.txt
poly ws get:settings:profile-to-settings:default:paths color-mode:none > $output/ws-state-paths.txt
poly ws get:keys color-mode:none > $output/ws-state-keys.txt
poly ws get:components:keys color-mode:none > $output/ws-state-components-keys.txt
poly ws get:components:user color-mode:none > $output/ws-state-components-user.txt
poly ws get:components:user-remote:lib-deps color-mode:none > $output/ws-state-components-user-remote-lib-deps.txt
poly ws get:old:user-input:args ws-file:ws.edn color-mode:none > $output/ws-state-ws-file.txt

cd $example/../../clojure-polylith-realworld-example-app

echo "### 15/25 Realworld example app ###"
poly info > $output/realworld/realworld-info.txt
echo "### 16/25 Realworld example app ###"
poly deps > $output/realworld/realworld-deps-interfaces.txt
echo "### 17/25 Realworld example app ###"
poly deps brick:article > $output/realworld/realworld-deps-interface.txt
echo "### 18/25 Realworld example app ###"
poly deps project:rb > $output/realworld/realworld-deps-components.txt
echo "### 19/25 Realworld example app ###"
poly deps project:rb brick:article > $output/realworld/realworld-deps-component.txt
echo "### 20/25 Realworld example app ###"
poly libs > $output/realworld/realworld-lib-deps.txt
echo "### 21/25 Realworld example app ###"
cp $example/realworld/workspace-compact.edn ./workspace.edn
poly libs > $output/realworld/realworld-lib-deps-compact.txt
cp $example/realworld/workspace.edn .

echo "### 22/25 Polylith toolsdeps1 ###"
cd $ws1
git clone git@github.com:polyfy/polylith.git
cd polylith
echo "### 23/25 Polylith toolsdeps1 ###"
poly info > $output/polylith1/info.txt
echo "### 24/25 Polylith toolsdeps1 ###"
poly libs > $output/polylith1/libs.txt
echo "### 25/25 Polylith toolsdeps1 ###"
poly deps > $output/polylith1/deps.txt

cd $output
./output.sh > ./output.txt

echo $example
echo $ws

echo "Elapsed: $((($SECONDS / 60) % 60)) min $(($SECONDS % 60)) sec"

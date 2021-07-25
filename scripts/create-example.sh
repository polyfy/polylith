#!/usr/bin/env bash

# brew install tree

SECONDS=0

set -e
root=$(pwd)/..
scripts=$(pwd)
examples=$(pwd)/../examples
output=$(pwd)/output
sections=$(pwd)/sections
ws=$(mktemp -d -t polylith-ws-$(date +%Y-%m-%d-%H%M%S))
ws1=$(mktemp -d -t polylith-ws1-$(date +%Y-%m-%d-%H%M%S))
ws2=$(mktemp -d -t polylith-ws2-$(date +%Y-%m-%d-%H%M%S))
ws3=$(mktemp -d -t usermanager-ws-$(date +%Y-%m-%d-%H%M%S))

echo $(pwd)
echo $ws
echo $ws1
cd $ws

echo "### 1/44 Workspace ###"
poly create w name:example top-ns:se.example :git-add
tree example > $output/workspace-tree.txt
cd example

echo "### 2/44 Development ###"
mkdir development/src/dev
cp $sections/development/lisa.clj development/src/dev
git add development/src/dev/lisa.clj

echo "### 3/44 Component ###"
poly create c name:user
tree . > ../component-tree.txt

cp $sections/component/user-core.clj components/user/src/se/example/user/core.clj
git add components/user/src/se/example/user/core.clj
cp $sections/component/user-interface.clj components/user/src/se/example/user/interface.clj
cp $sections/component/deps.edn .
poly info fake-sha:c91fdad > $output/component-info.txt

echo "### 4/44 Base ###"
poly create b name:cli
cd ..
tree example > $output/base-tree.txt
cd example
cp $sections/base/deps.edn .
cp $sections/base/cli-core.clj bases/cli/src/se/example/cli/core.clj

echo "### 5/44 Project ###"
poly create p name:command-line
cd ..
tree example > $output/project-tree.txt
cd example
cp $sections/project/deps.edn .
cp $sections/project/workspace.edn .
cp $sections/project/command-line-deps.edn projects/command-line/deps.edn

echo "### 6/44 Tools.deps ###"
cd projects/command-line
mkdir -p classes
clj -e "(compile,'se.example.cli.core)"

echo "### 7/44 Build ###"
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

echo "### 8/44 Git ###"
cd ../../..
poly info fake-sha:c91fdad > $output/git-info.txt
git log
poly diff > $output/git-diff.txt
git add --all
git commit -m "Created the user and cli bricks."
git log --pretty=oneline

echo "### 9/44 Tagging ###"
git tag -f stable-lisa
git log --pretty=oneline
poly info fake-sha:e7ebe68 > $output/tagging-info-1.txt
firstsha=`git log --pretty=oneline | tail -1 | cut -d " " -f1`
git tag v1.1.0 $firstsha
git tag v1.2.0
poly info since:release fake-sha:e7ebe68 > $output/tagging-info-2.txt
poly info since:previous-release fake-sha:c91fdad > $output/tagging-info-3.txt
git log --pretty=oneline

echo "### 10/44 Flags ###"
poly info :r fake-sha:e7ebe68 > $output/flags-info.txt

echo "### 11/44 Testing ###"
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
mkdir -p projects/command-line/test/project/command_line
cp $sections/testing/dummy_test.clj projects/command-line/test/project/command_line
git add projects/command-line/test/project/command_line/dummy_test.clj
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

echo "### 12/44 Profile ###"
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

echo "### 13/44 Configuration ###"
poly ws get:settings
poly ws get:settings:profile-to-settings:default:paths
poly ws get:keys
poly ws get:components:keys
poly ws out:ws.edn
poly info ws-file:ws.edn fake-sha:e7ebe68
poly ws get:old:user-input:args ws-file:ws.edn > $output/config-ws.txt

echo "### 14/44 Workspace state ###"
poly ws get:settings color-mode:none > $output/ws-state-settings.txt
poly ws get:settings:profile-to-settings:default:paths color-mode:none > $output/ws-state-paths.txt
poly ws get:keys color-mode:none > $output/ws-state-keys.txt
poly ws get:components:keys color-mode:none > $output/ws-state-components-keys.txt
poly ws get:components:user color-mode:none > $output/ws-state-components-user.txt
poly ws get:components:user-remote:lib-deps color-mode:none > $output/ws-state-components-user-remote-lib-deps.txt
poly ws get:old:user-input:args ws-file:ws.edn color-mode:none > $output/ws-state-ws-file.txt

echo "### 15/44 Copy doc-example ###"
rm -rf $examples/doc-example
cp -R $ws/example $examples/doc-example
rm -rf $examples/doc-example/.git
rm $examples/doc-example/.gitignore
rm $examples/doc-example/readme.md
rm $examples/doc-example/ws.edn
rm $examples/doc-example/logo.png
rm $examples/doc-example/bases/.keep
rm $examples/doc-example/components/.keep
rm $examples/doc-example/projects/.keep
rm $examples/doc-example/development/src/.keep

echo "### 16/44 Realworld example app ###"
cd $ws2
git clone git@github.com:furkan3ayraktar/clojure-polylith-realworld-example-app.git
cd clojure-polylith-realworld-example-app
git checkout polylith-issue-66
git tag stable-lisa

poly info fake-sha:f7082da > $output/realworld/realworld-info.txt
echo "### 17/44 Realworld example app ###"
poly deps > $output/realworld/realworld-deps-interfaces.txt
echo "### 18/44 Realworld example app ###"
poly deps brick:article > $output/realworld/realworld-deps-interface.txt
echo "### 19/44 Realworld example app ###"
poly deps project:rb > $output/realworld/realworld-deps-components.txt
echo "### 20/44 Realworld example app ###"
poly deps project:rb brick:article > $output/realworld/realworld-deps-component.txt
echo "### 21/44 Realworld example app ###"
poly libs > $output/realworld/realworld-lib-deps.txt
echo "### 22/44 Realworld example app ###"
cp $scripts/realworld/workspace-compact.edn ./workspace.edn
poly libs > $output/realworld/realworld-lib-deps-compact.txt
cp $scripts/realworld/workspace.edn .

echo "### 23/44 Polylith toolsdeps1 ###"
cd $ws1
git clone git@github.com:polyfy/polylith.git
cd polylith
echo "### 24/44 Polylith toolsdeps1 ###"
poly info > $output/polylith1/info.txt
echo "### 25/44 Polylith toolsdeps1 ###"
poly libs > $output/polylith1/libs.txt
echo "### 26/44 Polylith toolsdeps1 ###"
poly deps > $output/polylith1/deps.txt

poly migrate
echo "### 27/44 Polylith toolsdeps1 (migrated) ###"
poly info > $output/polylith1/info-migrated.txt
echo "### 28/44 Polylith toolsdeps1 (migrated) ###"
poly libs > $output/polylith1/libs-migrated.txt
echo "### 29/44 Polylith toolsdeps1 (migrated) ###"
poly deps > $output/polylith1/deps-migrated.txt

cd $ws3
git clone https://github.com/seancorfield/usermanager-example.git
cd usermanager-example
git checkout polylith
echo "### 30/44 Usermanager ###"
poly info > $output/usermanager/info.txt
echo "### 31/44 Usermanager ###"
poly libs > $output/usermanager/libs.txt
echo "### 32/44 Usermanager ###"
poly deps > $output/usermanager/deps.txt

cd $examples/local-dep
echo "### 33/44 examples/local-dep ###"
poly info fake-sha:aaaaa > $output/local-dep/info.txt
echo "### 34/44 examples/local-dep ###"
poly libs > $output/local-dep/libs.txt
echo "### 35/44 examples/local-dep ###"
poly deps > $output/local-dep/deps.txt
echo "### 36/44 examples/local-dep ###"
poly diff since:0aaeb58 > $output/local-dep/diff.txt
echo "### 37/44 examples/local-dep ###"
poly ws out:$output/local-dep/ws.edn :user-home
echo "### 38/44 examples/local-dep ###"
poly test :dev > $output/local-dep/test.txt

cd $examples/local-dep-old-format
echo "### 39/44 examples/local-dep-old-format ###"
poly info fake-sha:aaaaa > $output/local-dep-old-format/info.txt
echo "### 40/44 examples/local-dep-old-format ###"
poly libs > $output/local-dep-old-format/libs.txt
echo "### 41/44 examples/local-dep-old-format ###"
poly deps > $output/local-dep-old-format/deps.txt
echo "### 42/44 examples/local-dep-old-format ###"
poly diff since:0aaeb58 > $output/local-dep-old-format/diff.txt
echo "### 43/44 examples/local-dep-old-format ###"
poly ws out:$output/local-dep-old-format/ws.edn :user-home
echo "### 44/44 examples/local-dep-old-format ###"
poly test :dev > $output/local-dep-old-format/test.txt

cd $output
./output.sh > ./output.txt

echo $scripts
echo $ws

echo "Elapsed: $((($SECONDS / 60) % 60)) min $(($SECONDS % 60)) sec"

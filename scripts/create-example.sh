#!/usr/bin/env bash

# brew install tree

SECONDS=0

set -e
root=$(cd ../ && pwd)
scripts=$(pwd)
examples=$root/examples
output=$scripts/output
sections=$scripts/sections
ws=$(mktemp -d -t polylith-ws-$(date +%Y-%m-%d-%H%M%S))
ws1=$(mktemp -d -t polylith-ws1-$(date +%Y-%m-%d-%H%M%S))
ws2=$(mktemp -d -t polylith-ws2-$(date +%Y-%m-%d-%H%M%S))
ws3=$(mktemp -d -t usermanager-ws-$(date +%Y-%m-%d-%H%M%S))
ws4=$(mktemp -d -t local_dep-$(date +%Y-%m-%d-%H%M%S))

echo "root=$root"
echo "scripts=$scripts"
echo "examples=$examples"
echo "output=$output"
echo "sections=$sections"
echo "ws=$ws"
echo "ws1=$ws1"
echo "ws2=$ws2"
echo "ws3=$ws3"
echo "ws4=$ws4"

cd $root
brew upgrade clojure
clojure -A:dev:test -P

cd $ws

echo "### 1/50 Workspace ###"
poly create w name:example top-ns:se.example :git-add
tree example > $output/workspace-tree.txt
cd example

echo "### 2/50 Development ###"
mkdir development/src/dev
cp $sections/development/lisa.clj development/src/dev
git add development/src/dev/lisa.clj

echo "### 3/50 Component ###"
echo "current dir=$(pwd)"
poly create c name:user
tree . > ../component-tree.txt

cp $sections/component/user-core.clj components/user/src/se/example/user/core.clj
git add components/user/src/se/example/user/core.clj
cp $sections/component/user-interface.clj components/user/src/se/example/user/interface.clj
cp $sections/component/deps.edn .
poly info fake-sha:c91fdad > $output/component-info.txt

echo "### 4/50 Base ###"
poly create b name:cli
cd ..
tree example > $output/base-tree.txt
cd example
echo "current-dir=$(pwd)"
cp $sections/base/deps.edn .
cp $sections/base/cli-core.clj bases/cli/src/se/example/cli/core.clj

echo "### 5/50 Project ###"
echo "current-dir=$(pwd)"
poly create p name:command-line
cd ..
tree example > $output/project-tree.txt
cd example
cp $sections/project/deps.edn .
cp $sections/project/workspace.edn .
cp $sections/project/command-line-deps.edn projects/command-line/deps.edn

echo "### 6/50 Build ###"
echo "current-dir=$(pwd)"
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

echo "### 7/50 Git ###"
cd ../../..
echo "current-dir=$(pwd)"
poly info fake-sha:c91fdad > $output/git-info.txt
git log
poly diff > $output/git-diff.txt
git add --all
git commit -m "Created the user and cli bricks."
git log --pretty=oneline

echo "### 8/50 Tagging ###"
echo "current-dir=$(pwd)"
git tag -f stable-lisa
git log --pretty=oneline
poly info fake-sha:e7ebe68 > $output/tagging-info-1.txt
firstsha=`git log --pretty=oneline | tail -1 | cut -d " " -f1`
git tag v1.1.0 $firstsha
git tag v1.2.0
poly info since:release fake-sha:e7ebe68 > $output/tagging-info-2.txt
poly info since:previous-release fake-sha:c91fdad > $output/tagging-info-3.txt
git log --pretty=oneline

echo "### 9/50 Flags ###"
echo "current-dir=$(pwd)"
poly info :r fake-sha:e7ebe68 > $output/flags-info.txt

echo "### 10/50 Testing ###"
echo "current-dir=$(pwd)"
cp $sections/testing/user-core.clj components/user/src/se/example/user/core.clj
poly diff > $output/testing-diff.txt
poly info fake-sha:e7ebe68 > $output/testing-info-1.txt
cp $sections/testing/user-interface-test.clj components/user/test/se/example/user/interface_test.clj
set +e
poly test > $output/testing-test-failing.txt
sed -i '' -E "s/Execution time: [0-9]+/Execution time: x/g" $output/testing-test-failing.txt
set -e
cp $sections/testing/user-interface-test2.clj components/user/test/se/example/user/interface_test.clj
poly test > $output/testing-test-ok.txt
sed -i '' -E "s/Execution time: [0-9]+/Execution time: x/g" $output/testing-test-ok.txt
poly info :dev fake-sha:e7ebe68 > $output/testing-info-2.txt
poly info project:cl:dev fake-sha:e7ebe68 > $output/testing-info-3.txt

poly info fake-sha:e7ebe68 > $output/testing-info-3a.txt
poly info brick:cli fake-sha:e7ebe68 > $output/testing-info-3b.txt
poly info :no-changes fake-sha:e7ebe68 > $output/testing-info-3c.txt
poly info brick:cli :no-changes fake-sha:e7ebe68 > $output/testing-info-3d.txt

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
sed -i '' -E "s/Execution time: [0-9]+/Execution time: x/g" $output/testing-test-all.txt

echo "### 11/50 Profile ###"
echo "current-dir=$(pwd)"
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
sed -i '' -E "s/Execution time: [0-9]+/Execution time: x/g" $output/profile-test.txt

echo "### 12/50 Configuration ###"
echo "current-dir=$(pwd)"
poly ws get:settings
poly ws get:settings:profile-to-settings:default:paths
poly ws get:keys
poly ws get:components:keys
poly ws out:ws.edn
poly info ws-file:ws.edn fake-sha:e7ebe68
poly ws get:old:user-input:args ws-file:ws.edn > $output/config-ws.txt

echo "### 13/50 Workspace state ###"
echo "current-dir=$(pwd)"
sha=`git rev-list -n 1 stable-lisa`
poly ws get:settings replace:$ws:WS-HOME:$sha:SHA color-mode:none > $output/ws-state-settings.txt
poly ws get:settings:profile-to-settings:default:paths color-mode:none > $output/ws-state-paths.txt
poly ws get:keys color-mode:none > $output/ws-state-keys.txt
poly ws get:components:keys replace:$ws:WS-HOME color-mode:none > $output/ws-state-components-keys.txt
poly ws get:components:user replace:$ws:WS-HOME color-mode:none > $output/ws-state-components-user.txt
poly ws get:components:user-remote:lib-deps color-mode:none > $output/ws-state-components-user-remote-lib-deps.txt
poly ws get:old:user-input:args ws-file:ws.edn color-mode:none > $output/ws-state-ws-file.txt

echo "### 14/50 Copy doc-example ###"
echo "current-dir=$(pwd)"
cp $examples/doc-example/readme.txt $ws
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
cp $ws/readme.txt $examples/doc-example/readme.txt

echo "### 15/50 Realworld example app ###"
cd $ws2
echo "current-dir=$(pwd)"
git clone git@github.com:furkan3ayraktar/clojure-polylith-realworld-example-app.git
cd clojure-polylith-realworld-example-app
git checkout polylith-issue-66
git tag stable-lisa

poly info fake-sha:f7082da > $output/realworld/realworld-info.txt
echo "current-dir=$(pwd)"
echo "### 16/50 Realworld example app ###"
poly deps > $output/realworld/realworld-deps-interfaces.txt
echo "### 17/50 Realworld example app ###"
poly deps brick:article > $output/realworld/realworld-deps-interface.txt
echo "### 18/50 Realworld example app ###"
poly deps project:rb > $output/realworld/realworld-deps-components.txt
echo "### 19/50 Realworld example app ###"
poly deps project:rb :compact > $output/realworld/realworld-deps-components-compact.txt
echo "### 20/50 Realworld example app ###"
poly deps project:rb brick:article > $output/realworld/realworld-deps-component.txt
echo "### 21/50 Realworld example app ###"
poly libs > $output/realworld/realworld-lib-deps.txt
echo "### 22/50 Realworld example app ###"
cp $scripts/realworld/workspace-compact.edn ./workspace.edn
poly libs > $output/realworld/realworld-lib-deps-compact.txt
cp $scripts/realworld/workspace.edn .

echo "### 23/50 Polylith toolsdeps1 ###"
cd $ws1
echo "current-dir=$(pwd)"
git clone git@github.com:polyfy/polylith.git
cd polylith
echo "### 24/50 Polylith toolsdeps1 ###"
poly info fake-sha:40d2f62 :no-changes color-mode:none > $output/polylith1/info.txt
echo "### 25/50 Polylith toolsdeps1 ###"
poly libs color-mode:none > $output/polylith1/libs.txt
echo "### 26/50 Polylith toolsdeps1 ###"
poly deps color-mode:none > $output/polylith1/deps.txt

poly migrate
echo "current-dir=$(pwd)"
echo "### 27/50 Polylith toolsdeps1 (migrated) ###"
poly info fake-sha:40d2f62 :no-changes color-mode:none > $output/polylith1/info-migrated.txt
echo "### 28/50 Polylith toolsdeps1 (migrated) ###"
poly libs color-mode:none > $output/polylith1/libs-migrated.txt
echo "### 29/50 Polylith toolsdeps1 (migrated) ###"
poly deps color-mode:none > $output/polylith1/deps-migrated.txt

cd $ws3
git clone https://github.com/seancorfield/usermanager-example.git
cd usermanager-example
echo "current-dir=$(pwd)"
git checkout polylith
clojure -A:dev:test -P
echo "### 30/50 Usermanager ###"
poly info :no-changes color-mode:none > $output/usermanager/info.txt
echo "### 31/50 Usermanager ###"
poly libs color-mode:none > $output/usermanager/libs.txt
echo "### 32/50 Usermanager ###"
poly deps color-mode:none > $output/usermanager/deps.txt

cd $examples/local-dep
echo "current-dir=$(pwd)"
sha=`git rev-list -n 1 stable-master`
branch=`git rev-parse --abbrev-ref HEAD`
echo "### 33/50 examples/local-dep ###"
poly info color-mode:none fake-sha:aaaaa > $output/local-dep/info.txt
echo "### 34/50 examples/local-dep ###"
poly libs color-mode:none > $output/local-dep/libs.txt
echo "### 35/50 examples/local-dep ###"
poly libs :compact color-mode:none > $output/local-dep/libs-compact.txt
echo "### 36/50 examples/local-dep ###"
poly deps color-mode:none > $output/local-dep/deps.txt
echo "### 37/50 examples/local-dep ###"
poly deps :compact color-mode:none > $output/local-dep/deps-compact.txt
echo "### 38/50 examples/local-dep ###"
poly deps :compact project:inv color-mode:none > $output/local-dep/deps-project-compact.txt
echo "### 39/50 examples/local-dep ###"
poly diff since:0aaeb58 color-mode:none > $output/local-dep/diff.txt
echo "### 40/50 examples/local-dep ###"
poly ws out:$output/local-dep/ws.edn replace:$ws4:WS-HOME:$HOME:USER-HOME:$sha:SHA:$branch:BRANCH color-mode:none
echo "### 41/50 examples/local-dep ###"
poly test :dev since:0aaeb58 color-mode:none > $output/local-dep/test.txt
sed -i '' -E "s/Execution time: [0-9]+/Execution time: x/g" $output/local-dep/test.txt

echo "### 42/50 examples/local-dep-old-format ###"
cp -R $examples/local-dep-old-format $ws4
cd $ws4/local-dep-old-format
echo "current-dir=$(pwd)"
git init
git add .
git commit -m "Workspace created."
git tag stable-jote
sha=`git rev-list -n 1 stable-jote`

poly info fake-sha:aaaaa color-mode:none > $output/local-dep-old-format/info.txt
echo "### 43/50 examples/local-dep-old-format ###"
poly libs color-mode:none > $output/local-dep-old-format/libs.txt
echo "### 44/50 examples/local-dep-old-format ###"
poly deps color-mode:none > $output/local-dep-old-format/deps.txt
echo "### 45/50 examples/local-dep-old-format ###"
poly ws out:$output/local-dep-old-format/ws.edn replace:$ws4:WS-HOME:$HOME:USER-HOME:$sha:SHA color-mode:none
echo "### 46/50 examples/local-dep-old-format ###"
poly test :dev color-mode:none > $output/local-dep-old-format/test.txt
sed -i '' -E "s/Execution time: [0-9]+/Execution time: x/g" $output/local-dep-old-format/test.txt

poly migrate
git add --all
poly info fake-sha:aaaaa :no-changes color-mode:none > $output/local-dep-old-format/info-migrated.txt
echo "### 47/50 examples/local-dep-old-format (migrated) ###"
poly libs color-mode:none > $output/local-dep-old-format/libs-migrated.txt
echo "### 48/50 examples/local-dep-old-format (migrated) ###"
poly deps color-mode:none > $output/local-dep-old-format/deps-migrated.txt
echo "### 49/50 examples/local-dep-old-format (migrated) ###"
poly test :de color-mode:none > $output/local-dep-old-format/test-migrated.txt
sed -i '' -E "s/Execution time: [0-9]+/Execution time: x/g" $output/local-dep-old-format/test-migrated.txt

cd $output
./output.sh > ./output.txt

cd help

echo "### 50/50 help ###"

./help.sh
./help-doc.sh > ../../../doc/commands.md

echo "Elapsed: $((($SECONDS / 60) % 60)) min $(($SECONDS % 60)) sec"

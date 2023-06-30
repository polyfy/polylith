#!/usr/bin/env bash

#----------------------------------------------------------------------
# This script is used to run all the commands in the documentation
# and produces output files in the 'output' directory that serves
# these purposes:
# - it makes sure all the commands can be executed from a shell.
# - the output from these commands is version controlled and
#   is used to validate that everything works as expected.
# - the output from 'cat output/output.txt' is used to update
#   the images in the documentation.
# - different git commands are executed to ensure everything
#   works with git as expected.
# - the whole polylith repository itself is used as test workspace
#   both with the current :toolsdeps2 format but also the older
#   :toolsdeps1 format, to ensure we can run all the commands
#   against workspaces created with 'poly' version 0.1.0-alpha9 or
#   older.
# - we also run the commands agains the realworld and usermanager
#   repos and repos under the examples directory, to make sure they
#   work and that we get expected output from them.
#
# Before executing this command, make sure the 'tree' command is
# installed, by executing:
#   brew install tree
#
# Also make sure the 'poly' command is compiled, by executing:
#   ./build-plain.sh.
#
# To update the documentation:
#   cd output/help
#   ./update-commands-doc.sh
#----------------------------------------------------------------------

SECONDS=0

set -e
root=$(cd ../ && pwd)
scripts=$(pwd)
examples=$root/examples
output=$scripts/output
images=$scripts/images
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
brew upgrade clojure/tools/clojure
clojure -A:dev:test -P

cd $ws

echo "### 1/55 Workspace ###"
poly create w name:example top-ns:se.example :git-add :commit
#tree example > $output/workspace-tree.txt
cd example

echo "### 2/55 Development ###"
mkdir development/src/dev
cp $sections/development/lisa.clj development/src/dev
git add development/src/dev/lisa.clj

echo "### 3/55 Component ###"
echo "current dir=$(pwd)"
poly create c name:user
#tree . > ../component-tree.txt

cp $sections/component/user-core.clj components/user/src/se/example/user/core.clj
git add components/user/src/se/example/user/core.clj
cp $sections/component/user-interface.clj components/user/src/se/example/user/interface.clj
cp $sections/component/deps.edn .
poly info fake-sha:c91fdad out:$images/component-info.png

echo "### 4/55 Base ###"
poly create b name:cli
cd ..
#tree example > $output/base-tree.txt
cd example
echo "current-dir=$(pwd)"
cp $sections/base/deps.edn .
cp $sections/base/cli-core.clj bases/cli/src/se/example/cli/core.clj

echo "### 5/55 Project ###"
echo "current-dir=$(pwd)"
poly create p name:command-line
cd ..
#tree example > $output/project-tree.txt
cd example
cp $sections/project/deps.edn .
cp $sections/project/workspace1.edn ./workspace.edn
cp $sections/project/command-line-deps.edn projects/command-line/deps.edn

echo "### 6/55 Build ###"
echo "current-dir=$(pwd)"
cp $sections/build/build.clj .
cp $sections/build/command-line-deps.edn projects/command-line/deps.edn

clojure -T:build uberjar :project command-line
cd projects/command-line/target
java -jar command-line.jar Lisa

echo "### 7/55 Git ###"
cd ../../..
echo "current-dir=$(pwd)"
poly info fake-sha:c91fdad out:$images/git-info.png
git log
#poly diff > $output/git-diff.txt
git add --all
git commit -m "Created the user and cli bricks."
git log --pretty=oneline

echo "### 8/55 Tagging ###"
echo "current-dir=$(pwd)"
git tag -f stable-lisa
git log --pretty=oneline
poly info fake-sha:e7ebe68 out:$images/tagging-info-1.png
firstsha=`git log --pretty=oneline | tail -1 | cut -d " " -f1`
git tag v1.1.0 $firstsha
git tag v1.2.0

cp $sections/tagging/user-core-change.clj components/user/src/se/example/user/core.clj
poly info since:release fake-sha:e7ebe68 out:$images/tagging-info-2.png
#cp $sections/tagging/user-core.clj components/user/src/se/example/user/core.clj

poly info since:release fake-sha:e7ebe68 out:$images/tagging-info-3.png
poly info since:previous-release fake-sha:c91fdad out:$images/tagging-info-4.png
git log --pretty=oneline

echo "### 9/55 Flags ###"
echo "current-dir=$(pwd)"
poly info :r fake-sha:e7ebe68 out:$images/flags-info.png

echo "### 10/55 Testing ###"
echo "current-dir=$(pwd)"
cp $sections/testing/user-core.clj components/user/src/se/example/user/core.clj
#poly diff > $output/testing-diff.txt
poly info fake-sha:e7ebe68 out:$images/testing-info-1.png
cp $sections/testing/user-interface-test.clj components/user/test/se/example/user/interface_test.clj
set +e
#poly test out:$images/testing-test-failing.png
#echo "exit code: $?" >> $output/testing-test-failing.txt
#sed -i '' -E "s/Execution time: [0-9]+/Execution time: x/g" $output/testing-test-failing.txt
set -e
cp $sections/testing/user-interface-test2.clj components/user/test/se/example/user/interface_test.clj
#poly test > $output/testing-test-ok.txt
#echo "exit code: $?" >> testing-test-ok.txt
#sed -i '' -E "s/Execution time: [0-9]+/Execution time: x/g" $output/testing-test-ok.txt
clojure -A:dev:test -P
poly info :dev fake-sha:e7ebe68 out:$images/testing-info-2.png
poly info project:dev fake-sha:e7ebe68 out:$images/testing-info-2b.png
poly info project:cl:dev fake-sha:e7ebe68 out:$images/testing-info-3.png

poly info fake-sha:e7ebe68 out:$images/testing-info-3a.png
poly info brick:cli fake-sha:e7ebe68 out:$images/testing-info-3b.png
poly info :no-changes fake-sha:e7ebe68 out:$images/testing-info-3c.png
poly info brick:cli :no-changes fake-sha:e7ebe68 out:$images/testing-info-3d.png

mkdir projects/command-line/test
cp $sections/testing/deps.edn .
cp $sections/testing/command-line-deps.edn projects/command-line/deps.edn
mkdir -p projects/command-line/test/project/command_line
cp $sections/testing/dummy_test.clj projects/command-line/test/project/command_line
git add projects/command-line/test/project/command_line/dummy_test.clj
poly info fake-sha:e7ebe68 out:$images/testing-info-4.png
poly info :project fake-sha:e7ebe68 out:$images/testing-info-5.png
#poly test :project fake-sha:e7ebe68 > $output/testing-test-project.txt
#echo "exit code: $?" >> $output/testing-test-project.txt
git add --all
git commit -m "Added tests"
git tag -f stable-lisa
poly info fake-sha:e7ebe68 out:$images/testing-info-6.png
poly info :all-bricks fake-sha:e7ebe68 out:$images/testing-info-7.png
poly info :all-bricks :dev fake-sha:e7ebe68 out:$images/testing-info-8.png
poly info :all fake-sha:e7ebe68 out:$images/testing-info-9.png
poly info :all :dev fake-sha:e7ebe68 out:$images/testing-info-10.png

poly info :all :dev fake-sha:e7ebe68 out:$images/testing-info-11.png
#poly test :all :dev color-mode:none > $output/testing-test-all.txt
#echo "exit code: $?" >> $output/testing-test-all.txt
#sed -i '' -E "s/Execution time: [0-9]+/Execution time: x/g" $output/testing-test-all.txt

cp $sections/project/command-line-test-setup.clj projects/command-line/test/project/command_line/test_setup.clj
cp $sections/project/workspace2.edn ./workspace.edn

cp $sections/project/command-line-test-setup.clj projects/command-line/test/project/command_line/test_setup.clj
cp $sections/project/workspace2.edn ./workspace.edn

#poly test :all color-mode:none > $output/testing-test-fixture.txt
#echo "exit code: $?" >> $output/testing-test-fixture.txt

cp $sections/testing/workspace.edn .
poly info :all :dev fake-sha:e7ebe68 out:$images/testing-info-exclude-tests.png
#poly test :all :dev color-mode:none > $output/testing-test-all-exclude-tests.txt
#echo "exit code: $?" >> $output/testing-test-all-exclude-tests.txt
#sed -i '' -E "s/Execution time: [0-9]+/Execution time: x/g" $output/testing-test-all-exclude-tests.txt

echo "### 11/55 Profile ###"
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

clojure -T:build uberjar :project command-line
clojure -T:build uberjar :project user-service

#cd ../projects/user-service/target
#nohup 'java -jar service.jar' &
#cd ../../command-line/target
#java -jar command-line.jar Lisa

poly info + fake-sha:e7ebe68 out:$images/profile-info-1.png
poly info +remote fake-sha:e7ebe68 out:$images/profile-info-2.png
set +e
poly info +default +remote fake-sha:e7ebe68 out:$images/profile-info-3.png
set -e
poly info :loc fake-sha:e7ebe68 out:$images/profile-info-4.png
#poly test :project fake-sha:e7ebe68 > $output/profile-test.txt
#echo "exit code: $?" >> $output/profile-test.txt
#sed -i '' -E "s/Execution time: [0-9]+/Execution time: x/g" $output/profile-test.txt

echo "### 12/55 Configuration ###"
echo "current-dir=$(pwd)"
#poly ws get:settings
#poly ws get:settings:profile-to-settings:default:paths
#poly ws get:keys
#poly ws get:components:keys
#poly ws out:ws.edn
#poly info ws-file:ws.edn fake-sha:e7ebe68
#poly ws get:old:user-input:args ws-file:ws.edn > $output/config-ws.txt

echo "### 13/55 Workspace state ###"
echo "current-dir=$(pwd)"
sha=`git rev-list -n 1 stable-lisa`
#poly ws get:settings replace:$ws:WS-HOME:$sha:SHA color-mode:none > $output/ws-state-settings.txt
#poly ws get:settings:profile-to-settings:default:paths color-mode:none > $output/ws-state-paths.txt
#poly ws get:keys color-mode:none > $output/ws-state-keys.txt
#poly ws get:components:keys replace:$ws:WS-HOME color-mode:none > $output/ws-state-components-keys.txt
#poly ws get:components:user replace:$ws:WS-HOME color-mode:none > $output/ws-state-components-user.txt
#poly ws get:components:user-remote:lib-deps color-mode:none > $output/ws-state-components-user-remote-lib-deps.txt
#poly ws get:old:user-input:args ws-file:ws.edn color-mode:none > $output/ws-state-ws-file.txt

echo "### 14/55 Copy doc-example ###"
echo "current-dir=$(pwd)"
cp $examples/doc-example/readme.txt $ws
rm -rf $examples/doc-example
cp -R $ws/example $examples/doc-example
rm -rf $examples/doc-example/.git
rm $examples/doc-example/.gitignore
rm $examples/doc-example/readme.md
rm $examples/doc-example/logo.png
rm $examples/doc-example/bases/.keep
rm $examples/doc-example/components/.keep
rm $examples/doc-example/projects/.keep
rm $examples/doc-example/development/src/.keep
cp $ws/readme.txt $examples/doc-example/readme.txt

echo "### 15/55 Realworld example app ###"
cd $ws2
echo "current-dir=$(pwd)"
git clone git@github.com:furkan3ayraktar/clojure-polylith-realworld-example-app.git
cd clojure-polylith-realworld-example-app
clojure -A:dev:test -P
git tag stable-lisa

poly info fake-sha:f7082da > $output/realworld/realworld-info.txt
echo "current-dir=$(pwd)"
echo "### 16/55 Realworld example app ###"
poly deps out:$images/realworld/realworld-deps-interfaces.png
echo "### 17/55 Realworld example app ###"
poly deps brick:article out:$images/realworld/realworld-deps-interface.png
echo "### 18/55 Realworld example app ###"
poly deps project:rb out:$images/realworld/realworld-deps-components.png
echo "### 19/55 Realworld example app ###"
poly deps project:rb :compact out:$images/realworld/realworld-deps-components-compact.png
echo "### 20/55 Realworld example app ###"
poly deps project:rb brick:article out:$images/realworld/realworld-deps-component.png
echo "### 21/55 Realworld example app ###"
poly libs out:$images/realworld/realworld-lib-deps.png
echo "### 22/55 Realworld example app ###"
cp $scripts/realworld/workspace-compact.edn ./workspace.edn
poly libs out:$images/realworld/realworld-lib-deps-compact.png
cp $scripts/realworld/workspace.edn .

echo "### 23/55 Polylith toolsdeps1 ###"
cd $ws1
echo "current-dir=$(pwd)"
git clone git@github.com:polyfy/polylith.git
cd polylith
echo "### 24/55 Polylith toolsdeps1 ###"
poly info fake-sha:40d2f62 :no-changes color-mode:none out:$images/polylith1/info.png
echo "### 25/55 Polylith toolsdeps1 ###"
clojure -A:dev:test -P
poly libs color-mode:none out:$images/polylith1/libs.png
echo "### 26/55 Polylith toolsdeps1 ###"
poly deps color-mode:none out:$images/polylith1/deps.png

poly migrate
echo "current-dir=$(pwd)"
echo "### 27/55 Polylith toolsdeps1 (migrated) ###"
poly info fake-sha:40d2f62 :no-changes color-mode:none out:$images/polylith1/info-migrated.png
echo "### 28/55 Polylith toolsdeps1 (migrated) ###"
poly libs color-mode:none out:$images/polylith1/libs-migrated.png
echo "### 29/55 Polylith toolsdeps1 (migrated) ###"
poly deps color-mode:none out:$images/polylith1/deps-migrated.png

cd $ws3
git clone https://github.com/seancorfield/usermanager-example.git
cd usermanager-example
echo "current-dir=$(pwd)"
git checkout polylith
clojure -A:dev:test -P
echo "### 30/55 Usermanager ###"
poly info :no-changes color-mode:none out:$images/usermanager/info.png
echo "### 31/55 Usermanager ###"
poly libs color-mode:none out:$images/usermanager/libs.png
echo "### 32/55 Usermanager ###"
poly deps color-mode:none out:$images/usermanager/deps.png

cd $examples/local-dep
echo "current-dir=$(pwd)"
sha=`git rev-list -n 1 stable-master`
branch=`git rev-parse --abbrev-ref HEAD`
echo "### 33/55 examples/local-dep ###"
poly info color-mode:none fake-sha:aaaaa :no-changes out:$images/local-dep/info.png
echo "### 34/55 examples/local-dep ###"
poly libs color-mode:none out:$images/local-dep/libs.png
echo "### 35/55 examples/local-dep ###"
poly libs :compact color-mode:none out:$images/local-dep/libs-compact.png
echo "### 36/55 examples/local-dep ###"
poly deps color-mode:none out:$images/local-dep/deps.png
echo "### 37/55 examples/local-dep ###"
poly deps :compact color-mode:none out:$images/local-dep/deps-compact.png
echo "### 38/55 examples/local-dep ###"
poly deps :compact project:inv color-mode:none out:$images/local-dep/deps-project-compact.png
echo "### 39/55 examples/local-dep ###"
#poly diff since:0aaeb58 color-mode:none > $output/local-dep/diff.txt
echo "### 40/55 examples/local-dep ###"
#poly ws out:$output/local-dep/ws.edn replace:$ws4:WS-HOME:$HOME:USER-HOME:$sha:SHA:$branch:BRANCH color-mode:none
echo "### 41/55 examples/local-dep ###"
poly info :dev since:0aaeb58 color-mode:none out:$images/local-dep/since-info.png
#poly test :dev since:0aaeb58 color-mode:none > $output/local-dep/test.txt
#echo "exit code: $?" >> $output/local-dep/test.txt
#sed -i '' -E "s/Execution time: [0-9]+/Execution time: x/g" $output/local-dep/test.txt

echo "### 42/55 examples/local-dep-old-format ###"
cp -R $examples/local-dep-old-format $ws4
cd $ws4/local-dep-old-format
echo "current-dir=$(pwd)"
git init
git add .
git commit -m "Workspace created."
git tag stable-jote
sha=`git rev-list -n 1 stable-jote`

poly info fake-sha:aaaaa color-mode:none :no-changes out:$images/local-dep-old-format/info.png
echo "### 43/55 examples/local-dep-old-format ###"
poly libs color-mode:none out:$images/local-dep-old-format/libs.png
echo "### 44/55 examples/local-dep-old-format ###"
poly deps color-mode:none out:$images/local-dep-old-format/deps.png
echo "### 45/55 examples/local-dep-old-format ###"
#poly ws out:$output/local-dep-old-format/ws.edn replace:$ws4:WS-HOME:$HOME:USER-HOME:$sha:SHA color-mode:none
echo "### 46/55 examples/local-dep-old-format ###"
#poly test :dev color-mode:none > $output/local-dep-old-format/test.txt
#echo "exit code: $?" >> $output/local-dep-old-format/test.txt
#sed -i '' -E "s/Execution time: [0-9]+/Execution time: x/g" $output/local-dep-old-format/test.txt

poly migrate
git add --all
poly info fake-sha:aaaaa :no-changes color-mode:none out:$images/local-dep-old-format/info-migrated.png
echo "### 47/55 examples/local-dep-old-format (migrated) ###"
poly libs color-mode:none out:$images/local-dep-old-format/libs-migrated.png
echo "### 48/55 examples/local-dep-old-format (migrated) ###"
poly deps color-mode:none out:$images/local-dep-old-format/deps-migrated.png
echo "### 49/55 examples/local-dep-old-format (migrated) ###"
#poly test :de color-mode:none > $output/local-dep-old-format/test-migrated.txt
#echo "exit code: $?" >> $output/local-dep-old-format/test-migrated.txt
#sed -i '' -E "s/Execution time: [0-9]+/Execution time: x/g" $output/local-dep-old-format/test-migrated.txt

cd $examples/for-test
echo "current-dir=$(pwd)"

set +e
echo "### 50/55 examples/for-test, issue 208 - Mix clj and cljc source directories ###"
#poly test :all project:okay color-mode:none > $output/for-test/mix-clj-and-cljc.txt
#echo "exit code: $?" >> $output/for-test/mix-clj-and-cljc.txt
echo "### 51/55 examples/for-test, issue 234 - Test setup fails ###"
#poly test :all project:okay:setup-fails:x-okay color-mode:none > $output/for-test/setup-fails-stops-entire-test-run.txt
#echo "exit code: $?" >> $output/for-test/setup-fails-stops-entire-test-run.txt
echo "### 52/55 examples/for-test, issue 234 - Test teardown fails ###"
#poly test :all project:okay:teardown-fails:x-okay color-mode:none > $output/for-test/teardown-fails-stops-entire-test-run.txt
#echo "exit code: $?" >> $output/for-test/teardown-fails-stops-entire-test-run.txt
echo "### 53/55 examples/for-test, issue 234 - Test failed ###"
#poly test :all project:failing-test:okay color-mode:none > $output/for-test/failing-test-runs-teardown-and-stops-entire-test-run.txt
#echo "exit code: $?" >> $output/for-test/failing-test-runs-teardown-and-stops-entire-test-run.txt
echo "### 54/55 examples/for-test, issue 234 - Test failed, teardown failed ###"
#poly test :all project:failing-test-teardown-fails:okay color-mode:none > $output/for-test/failing-test-and-teardown-fails-stops-entire-test-run.txt
#echo "exit code: $?" >> $output/for-test/failing-test-and-teardown-fails-stops-entire-test-run.txt
set -e

cd $output
./output.sh > ./output.txt

cd help

echo "### 55/55 help ###"

./update-commands-doc.sh

echo "Elapsed: $((($SECONDS / 60) % 60)) min $(($SECONDS % 60)) sec"

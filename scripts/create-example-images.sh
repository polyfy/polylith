#!/usr/bin/env bash

#---------------------------------------------------------------------------
# Make sure to run the build-extended.sh script before running this script.
#
# This script runs all the commands in the documentation and produces
# output image files in the 'images' directory.
# 
# This script needs to be manually updated to stay in sync with 
# create-example.sh
#---------------------------------------------------------------------------

SECONDS=0

set -e
root=$(cd ../ && pwd)
scripts=$(pwd)
examples=$root/examples
output=$scripts/output
images=$root/doc/images
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

echo "### 1/56 Workspace ###"
poly create workspace name:example top-ns:se.example :git-add :commit
cd example

echo "### 2/56 Development ###"
mkdir development/src/dev
cp $sections/development/lisa.clj development/src/dev
git add development/src/dev/lisa.clj

echo "### 3/56 Component ###"
echo "current dir=$(pwd)"
poly create component name:user

cp $sections/component/user-core.clj components/user/src/se/example/user/core.clj
git add components/user/src/se/example/user/core.clj
cp $sections/component/user-interface.clj components/user/src/se/example/user/interface.clj
cp $sections/component/deps.edn .
poly info fake-sha:c91fdad out:$images/component/info.png

echo "### 4/56 Base ###"
poly create b name:cli
cd ..
cd example
echo "current-dir=$(pwd)"
cp $sections/base/deps.edn .
cp $sections/base/cli-core.clj bases/cli/src/se/example/cli/core.clj

echo "### 5/56 Project ###"
echo "current-dir=$(pwd)"
poly create project name:command-line
cd ..
cd example
cp $sections/project/deps.edn .
cp $sections/project/workspace1.edn ./workspace.edn
cp $sections/project/command-line-deps.edn projects/command-line/deps.edn

echo "### 6/56 Polyx ###"
echo "current-dir=$(pwd)"
cp $sections/polyx/deps.edn .
clojure -M:polyx info fake-sha:c91fdad out:$images/polyx/info.png
clojure -M:polyx overview fake-sha:c91fdad out:$images/polyx/overview.png
cp $sections/project/deps.edn .

echo "### 7/56 Tools.deps ###"

echo "### 8/56 Build ###"
echo "current-dir=$(pwd)"
cp $sections/build/deps.edn .
cp $sections/build/build.clj .
cp $sections/build/command-line-deps.edn projects/command-line/deps.edn

echo "### 9/56 Git ###"
echo "current-dir=$(pwd)"
poly info fake-sha:c91fdad out:$images/git/info.png
git add --all
git commit -m "Created the user and cli bricks."

echo "### 10/56 Tagging ###"
echo "current-dir=$(pwd)"
git tag -f stable-lisa
git log --pretty=oneline
poly info fake-sha:e7ebe68 out:$images/tagging/info-01.png
firstsha=`git log --pretty=oneline | tail -1 | cut -d " " -f1`
git tag v1.1.0 $firstsha
git tag v1.2.0

cp $sections/tagging/user-core-change.clj components/user/src/se/example/user/core.clj
poly info since:release fake-sha:e7ebe68 out:$images/tagging/info-02.png

poly info since:release fake-sha:e7ebe68 out:$images/tagging/info-03.png
poly info since:previous-release fake-sha:c91fdad out:$images/tagging/info-04.png

echo "### 11/56 Flags ###"
echo "current-dir=$(pwd)"
poly info :resources fake-sha:e7ebe68 out:$images/flags/info.png

echo "### 12/56 Testing ###"
echo "current-dir=$(pwd)"
cp $sections/testing/user-core.clj components/user/src/se/example/user/core.clj
poly info fake-sha:e7ebe68 out:$images/testing/info.png
cp $sections/testing/user-interface-test2.clj components/user/test/se/example/user/interface_test.clj
clojure -A:dev:test -P
poly info :dev fake-sha:e7ebe68 out:$images/testing/info-dev.png
poly info project:dev fake-sha:e7ebe68 out:$images/testing/info-project-dev.png
poly info project:cl:dev fake-sha:e7ebe68 out:$images/testing/info-project-cl-dev.png

poly info fake-sha:e7ebe68 out:$images/testing/info-filter-on-bricks.png
poly info brick:cli fake-sha:e7ebe68 out:$images/testing/info-brick-cli.png
poly info :no-changes fake-sha:e7ebe68 out:$images/testing/info-no-changes.png
poly info brick:cli :no-changes fake-sha:e7ebe68 out:$images/testing/info-brick-cli-no-changes.png
poly info :no-changes brick:cli :all-bricks fake-sha:e7ebe68 out:$images/testing/info-brick-cli-no-changes-all-bricks.png

mkdir projects/command-line/test
cp $sections/testing/deps.edn .
cp $sections/testing/command-line-deps.edn projects/command-line/deps.edn
mkdir -p projects/command-line/test/project/command_line
cp $sections/testing/dummy_test.clj projects/command-line/test/project/command_line
git add projects/command-line/test/project/command_line/dummy_test.clj
poly info fake-sha:e7ebe68 out:$images/testing/info-project-dir.png
poly info :project fake-sha:e7ebe68 out:$images/testing/info-project.png
git add --all
git commit -m "Added tests"
git tag -f stable-lisa
poly info fake-sha:e7ebe68 out:$images/testing/info-added-tests.png
poly info :all-bricks fake-sha:e7ebe68 out:$images/testing/info-all-bricks.png
poly info :all-bricks :dev fake-sha:e7ebe68 out:$images/testing/info-all-bricks-dev.png
poly info :all fake-sha:e7ebe68 out:$images/testing/info-all.png
poly info :all :dev fake-sha:e7ebe68 out:$images/testing/info-all-dev.png

cp $sections/testing/command-line-test-setup.clj projects/command-line/test/project/command_line/test_setup.clj
cp $sections/testing/workspace.edn .

echo "### 13/56 Profile ###"
echo "current-dir=$(pwd)"

cp $sections/profile/workspace.edn .
poly create project name:user-service
poly create base name:user-api
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

poly info + fake-sha:e7ebe68 out:$images/profile/info-all-aliases.png
poly info +remote fake-sha:e7ebe68 out:$images/profile/info-with-remote-profile.png
# We don't include the error message today, so we can't use the generated image.
# poly info +default +remote fake-sha:e7ebe68 out:$images/profile/info-multiple-profiles.png
poly info :loc fake-sha:e7ebe68 out:$images/profile/info-loc.png

echo "### 14/56 Configuration ###"
echo "current-dir=$(pwd)"

echo "### 15/56 Workspace state ###"
echo "current-dir=$(pwd)"

echo "### 16/56 Copy doc-example ###"
echo "current-dir=$(pwd)"

echo "### 17/56 Realworld example app ###"
cd $ws2
echo "current-dir=$(pwd)"
git clone git@github.com:furkan3ayraktar/clojure-polylith-realworld-example-app.git
cd clojure-polylith-realworld-example-app
clojure -A:dev:test -P
git tag stable-lisa

echo "Elapsed: $((($SECONDS / 60) % 60)) min $(($SECONDS % 60)) sec"

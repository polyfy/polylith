Polylith repository has a continuous integration and deployment setup via [CircleCI](https://circleci.com). The setup has two workflows, one for regular validation and testing and another one for deployments. Both workflows have several jobs to validate, test, and deploy the artifacts to [Clojars](https://clojars.org) and GitHub as [releases](https://github.com/polyfy/polylith/releases).

You can find the CircleCI configuration file [here](../.circleci/config.yml).

# 1. Validate Workflow
The main responsibility of this workflow is to validate the workspace and run the tests incrementally for all commits to all branches. Specifically for master branch, it also marks the commit as stable via a git tag.

## Jobs

### Check
This job runs the check command from Polylith as follows: ```clojure -M:poly check```. If there are any errors in the Polylith workspace, it returns with a non-zero exit code and the CircleCI workflow stops at this stage. If there are any warnings printed by Polylith, it will be visible in the job's output.

### Info
This job runs the following commands one after another:
- ```clojure -M:poly ws```
  - Prints the current workspace as data in [edn format](https://github.com/edn-format/edn).
- ```clojure -M:poly info```
  - Prints workspace information.
- ```clojure -M:poly deps```
  - Prints the dependency information
- ```clojure -M:poly libs```
  - Prints all libraries that are used in the workspace.

After this job is done, all this information will be available in the jobs output for debugging purposes if needed. You can read more about available commands [here](commands.md).

### Test
This job runs all the tests for all the bricks and projects that are directly or indirectly changed since the last stable point. Polylith supports incremental testing out of the box by using stable point marks in the git history. It runs the following command: ```clojure -M:poly test :project```. If any of the tests fail, it will exit with a non-zero exit code and the CircleCI workflow stops at this stage. Information about the passed/failed tests will be printed in the job's output.

### Mark as stable
This job only runs for the commits made to master branch. It adds (or moves if there is already one) the `stable-master` tag to the repository. At this point in the workflow, it is proven that the Polylith workspace is valid and all of the tests are passed. It is safe to mark this commit as stable. It does that by running following commands one after another:
- ```git tag -f -a "stable-$CIRCLE_BRANCH" -m "[skip ci] Added Stable Polylith tag"```
  - Creates or moves the tag
- ```git push origin $CIRCLE_BRANCH --tags --force```
  - Pushed tag back to the git repository

# 2. Deploy Workflow
The main responsibility of this workflow is to validate the workspace, run the tests incrementally and deploy artifacts to Clojars and GitHub releases. It only runs when a new tag is pushed to the git repository with the following pattern: `/^v.*/`.

## Jobs

### Check Build
This job runs the check command from Polylith as follows: ```clojure -M:poly check since:previous-release```. Please note the last part of the command where we specifically tell Polylith to check the workspace since the last release instead of the last stable point. If there are any errors in the Polylith workspace, it returns with a non-zero exit code and the CircleCI workflow stops at this stage. If there are any warnings printed by Polylith, it will be visible in the job's output.

### Info
This job runs the following commands one after another. Please note the last part of the command where we specifically say Polylith to check the workspace since the last release instead of the last stable point:
- ```clojure -M:poly ws since:previous-release```
  - Prints the current workspace as data in [edn format](https://github.com/edn-format/edn).
- ```clojure -M:poly info since:previous-release```
  - Prints workspace information.
- ```clojure -M:poly deps since:previous-release```
  - Prints the dependency information
- ```clojure -M:poly libs since:previous-release```
  - Prints all libraries that are used in the workspace.

After this job is done, all this information will be available in the jobs output for debugging purposes if needed. You can read more about available commands [here](commands.md).

### Test
This job runs all the tests for all the bricks that are directly or indirectly changed since the last release. It runs the following command: ```clojure -M:poly test :project since:previous-release```. If any of the tests fail, it will exit with a non-zero exit code and the CircleCI workflow stops at this stage. Information about the passed/failed tests will be printed in the job's output.

### Deploy
This job deploys the changed projects to Clojars. It is easy to achieve incremental deployments with Polylith. Changed projects are calculated since the latest release. You can see how it's done [here](https://github.com/polyfy/polylith/blob/master/build.clj). In a nutshell, it executes `poly ws get:changes:changed-or-affected-projects skip:dev since:previous-release` and only deploys the returned projects. 

### Create Artifacts
This job creates two types of artifacts per changed project, an aot compiled uberjar and a package that can be used to deploy [Homebrew](https://brew.sh). Created artifacts can be found in the artifacts section of this job's output.

### Publish GitHub Release
This job uploads the artifacts created after the previous job and uploads them to a new release in GitHub. It makes use of [GHR](https://github.com/tcnksm/ghr) tool in order to create a new release on GitHub and upload the artifacts.

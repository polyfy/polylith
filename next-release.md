
## Included since 0.2.21

| Issue                                                | Description  |
|:-----------------------------------------------------|--------------|
| [482](https://github.com/polyfy/polylith/issues/481) | Add support for ClojureScript
| [508](https://github.com/polyfy/polylith/issues/508) | Allow slash in profile names
| [509](https://github.com/polyfy/polylith/issues/509) | Include warnings in the check function in the API 
| [530](https://github.com/polyfy/polylith/issues/530) | Cause poly test to fail when no tests are run (optionally using extra arg to the command)
| [532](https://github.com/polyfy/polylith/issues/532) | Use :mvn/local-repo if specified in a project when running tests
| [533](https://github.com/polyfy/polylith/issues/533) | Maps with tag literals as keys in test namespaces breaks test runner
| [534](https://github.com/polyfy/polylith/issues/534) | Use correct regex in :tag-patterns in workspace.edn

| PR                                                 | Author          | Description                                                  
|:---------------------------------------------------|-----------------|------------------------------------------------------------|
| [517](https://github.com/polyfy/polylith/pull/517) | Sean Corfield   | Change Clojure version from 1.11.1 to 1.12.0               |
| [519](https://github.com/polyfy/polylith/pull/519) | Sean Corfield   | Ignore Calva's new REPL file                               |
| [520](https://github.com/polyfy/polylith/pull/520) | Sean Corfield   | Using Selmer templates for create fragments                |
| [521](https://github.com/polyfy/polylith/pull/521) | Sean Corfield   | Update circleci to use Clojure 1.12                        |
| [522](https://github.com/polyfy/polylith/pull/522) | Jeroen van Dijk | Update dep info of polylith-external-test-runner to v0.6.1 |
| [535](https://github.com/polyfy/polylith/pull/535) | [brettatoms](https://github.com/brettatoms) | fix comparing maven version numbers |

| Other changes                                                                                                                    |
|----------------------------------------------------------------------------------------------------------------------------------|
| If no project other than development, and we include development in a test run, then only print that there are not tests to run. |
| Print improved error message if edamame can't parse a file.                                                                      | 

| Doc updates                                                                                                                                                                                                                    |
|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|

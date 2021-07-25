
## Migrate workspace

If you have an old workspace and start using [v0.2.0-alpha10](https://github.com/polyfy/polylith/releases/tag/v0.2.0-alpha10)
or later, then all commands will still work except for the
[create component](commands.md#create-component),
[create base](commands.md#create-base) and
[create project](commands.md#create-project) commands.
Another difference is that the [ws](commands.md#ws) command will create files with a richer format
than before, which is not backward compatible with the previous format.
The good thing is that old `ws` files can still be read by the latest `poly` tool but will lack some
information like library dependencies for the `test` context which was not calculated prior to
version v0.2.0-alpha10.

The advice is therefore to migrate as soon as possible! 

A workspace can be migrated by executing the [migrate](commands.md#migrate) command. 
A migration will perform these steps:
- Extract the `:polylith` key from `./deps.edn` and create a `workspace.edn` file with the same
  information including a few changes to its format:
    - `:vcs` becomes a map with the keys `:name` and `:auto-add` where the latter determines
      whether files should automatically be added to `git` when the `create` command is used
      (`create workspace` is an exception, which will always perform an initial commit to git).
  - `:stable-tag-pattern` and `:release-tag-pattern` is now stored in `:tag-patterns` as the
    keys `stable` and `release`.
  - `:ns-to-lib` is removed.
  - `:project-to-alias` is replaced by `:projects` where each project stores a map
    with the name as its key and a map as value with the key `:name` and optionally `:test`
    that can be set to `[]` if no tests should be executed from that project.
- Each brick will get its own `deps.edn` file that specifies the `src`, `test` and `resources`
  directories + dependencies to libraries if any. The old `:ns-to-lib` mapping is used to figure
  out which libraries are used (before it's scrapped) and the versions to use are picked from the `./deps.edn` file.
- All the `deps.edn` files under the `projects` directory are updated for each project:
  - Paths to bricks are replaced by `:local/root` dependencies and libraries that are defined
    by these bricks are removed from the project, and will instead be implicitly included via
    `:local/root` definitions (which also means that you don't have to explicitly specify the `src`, `resources` 
    and `test` directories anymore, only the bricks). Notice that `test` directories are not inherited by `tools.deps`
    when `:local-root` dependencies are used, but that they _are_ included when you run the built-in poly `test`
    command. If you rely on `test` paths via other tooling, you may therefore keep these paths.
  - Notice that the `development` environment, with its `./deps.edn` file, is not converted to use `:local/root`
    and the reason is that the `:local/root` definitions will not be picked up by all IDE's as source code.
- Finally, the migration command tells you to manually remove the `:polylith` key from `./deps.edn`.

Before you start the migration, here are some tips:
- Decide how to run the `migrate` command, either by executing `poly migrate` or `clojure -M:poly migrate`.
  If you choose the first alternative, make sure you have the latest version of the `poly` tool. 
  If you choose the second alternative, make sure you update the `sha` of the `:poly` alias in `./deps.edn` to the latest
  sha.
- Make sure the workspace doesn't have any uncommitted changes.
- It can be an idea to run the migration in a separate git branch and merge it back when everything works
  (this is not mandatory).
- Update the `:ns-to-lib` key in `./deps.edn` and make sure you use the correct library 
  versions in `./deps.edn`. This will make your life easier because both are used 
  when the `deps.edn` files are created for the bricks.
- Run all the tests to make sure that everything works correctly, e.g. `poly test :all`.
- Run the `info` and `libs` commands and store the result to files, e.g.: `poly info > info.txt` and `poly libs > libs.txt`. 
  
After the migration:
- Run the `info` and `libs` commands again and make sure they have the same output as before the migration.
- Fix formatting and ordering to your taste. Unfortunately, all libraries will end up sorted
  so you may want to fix the ordering, e.g. letting bricks come first followed by the library dependencies.
- Go through the paths and dependencies in the `deps.edn` files under the `projects` directory
  and remove misplaced libraries from the project's `deps.edn` file and add them to the corresponding brick's `deps.edn` file.
  This is only needed for dependencies that were not specified in `:ns-to-lib`. If libraries are correctly
  specified in each brick's `deps.edn` file they will also show up under each brick when running the `libs` command.
- Run all the tests, e.g. `poly test :all`, and make sure everything passes.
- When everything works, commit and push to git!

Another benefit of this update is that you will git rid of the warning from `tools.deps` that relative paths will stop working in the future 
(e.g. `"../../components/my-component/src"`) which also is the main reason for this update.

You can find an example of how a project looks like before and after a migration in
`examples/local-dep-old-format` and `examples/local-dep`.

Good luck with this update of the tool!

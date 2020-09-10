# <img src="images/logo.png" width="50%" alt="Polylith" id="logo">
A tool used to develop Polylith based architectures.

---------

Welcome to the wonderful world of Polylith!

This implementation of Polylith works with [Clojure](https://clojure.org), which is a powerful functional language for the JVM.

Polylith is a new way of thinking around system architecture, 
that puts the developer in the driving seat and the code in the center.

Polylith is a way of organising code into reusable building blocks that are used to create systems. 
To better understand the principles and ideas behind it, we recommend you first read the Polylith 
[documentation](https://polylith.gitbook.io).

Organising code as a Polylith can be done manually, which was actually how it all began. With that said, 
there is no magic behind this way of organising the code. It's not a framework nor a library, 
just a simple yet powerful way to work with code at the system level.

The reason we built this tool is to make life easier for you as a developer by making the work more 
efficient and fun. 

Enjoy the ride!

## Migrate

The old lein-polylith tool has reached its end of lifetime. If you have any old Leiningen based projects 
that need to be migrated, follow the instructions [here](https://github.com/tengstrand/lein-polylith/blob/migrate/migrate/migrate.md).

## Table of Contents

- [Installation](#installation)
- [Realworld Example](#realworld-example)
- [Workspace](#workspace)
- [Development](#development)
- [Component](#component)
- [Base](#base)
- [Environment](#environment)
- [Build](#build)
- [Git](#git)
- [Tagging](#tagging)
- [Flags](#flags)
- [Test](#test)
- [Colors](#colors)

## Installation

To use the Polylith tool and to get access to all the features in tools.deps, follow these steps:
- Make sure [git](https://git-scm.com/book/en/v2/Getting-Started-Installing-Git) is installed.
- Install the [clj](https://clojure.org/guides/getting_started) command line tool.

Make sure that `user.email` and `user.name` is configured correctly in git, by e.g. executing:
```sh
git config --list | grep user
```
If not, check it [here](https://docs.github.com/en/github/using-git/setting-your-username-in-git).

*** verify the installation ***

The next thing we want to do is to download and install the `poly` command line tool.

*** instructions on how to install 'poly' in unix/linux and windows + verify ***

## Realworld Example

If you want to have a look at a full-blown system, go to the [RealWorld](https://github.com/furkan3ayraktar/clojure-polylith-realworld-example-app) project where you can compare it with [implementations made in other languages](https://github.com/gothinkster/realworld).

## Workspace

The workspace directory is the place where all our code (Lego-like bricks) and configuration lives.

Let’s start by creating the _example_ workspace with the top namespace _se.example_:
```sh
poly create w name:example top-ns:se.example
``` 

The workspace directory structure will end up like this:
```sh
example           # workspace dir
  bases           # bases dir (empty)
  components      # components dir (empty)
  development
    src           # development specific code
  environments    # environments dir (empty)
  deps.edn        # development environment config file (and some workspace config)
  logo.png        # polylith logo
  readme.md       # documentation
```

This directory structure helps us work with, and reason about the code and the higher level structure. 
Each top directory is responsible for its own part of a Polylith system.
A `base` exposes a public API. A `component` is responsible for a specific domain 
or part of the system. 
An `environment` specifies our deployable artifacts and what components and bases they contain.
Finally, we have the `development` environment that we use when we work with the code.
 
_deps.edn_:

```clojure
{:polylith {:vcs "git"
            :top-namespace "se.example"
            :interface-ns "interface"
            :default-profile-name "default"
            :stable-since-tag-pattern "stable-*"
            :env->alias {"development" "dev"}
            :ns->lib {}}

 :aliases  {:dev {:extra-paths ["development/src"]
                  :extra-deps {org.clojure/clojure {:mvn/version "1.10.1"}
                               org.clojure/tools.deps.alpha {:mvn/version "0.8.695"}}}

            :test {:extra-paths []}

            :poly {:main-opts ["-m" "polylith.clj.core.poly_cli.poly"]
                   :extra-deps {tengstrand/polylith
                                {:git/url   "https://github.com/tengstrand/polylith.git"
                                 :sha       "69e70df8882f4d9a701ab99681a4a6870bdf052b"
                                 :deps/root "environments/cli"}}}}}
```

We will later cover what all the different settings mean and how to use them.

If you are new to tools.deps then it could be a good idea to 
[read about](https://github.com/clojure/tools.deps.alpha) the ideas behind the tool and how things 
like _aliases_ are used. If you are already familiar with tools.deps, then you know that aliases 
are used to specify what source code and libraries an environment, or part of an environment, contains.

## Development

When working with a Polylith codebase, we are free to choose any editor/IDE we like, for example
Emacs/[Cider](https://github.com/clojure-emacs/cider), 
VSCode/[Calva](https://marketplace.visualstudio.com/items?itemName=betterthantomorrow.calva) or
IDEA/[Cursive](https://cursive-ide.com). Here we will use Cursive.

Let's get started by creating a project. From the menu, select `File > New > Project from existing source`.
Select the `deps.edn` file, the desired version of SDK and finish the wizard.

Make sure to activate the `:dev` alias (and press the "two arrows" icon to refresh):<br>
<img src="images/dev-alias.png" width="30%" alt="Dev alias">

Let's create a REPL by clicking `Add Configuration`:<br>
<img src="images/add-configuration.png" width="20%" alt="Add configuration">

Click the `+` sign and select `Clojure REPL > Local`:<br>
<img src="images/new-local-repl.png" width="30%" alt="New local REPL">

Fill in:
- Name: REPL
- Which type of REPL to run: nREPL
- Run with Deps: (select)
- Aliases: test,dev

Now start the REPL in debug mode, by clicking the bug icon:<br>
<img src="images/repl-start.png" width="20%" alt="New local REPL">

When this turns up:
```
nREPL server started on port 53536 on host localhost - nrepl://localhost:53536
Clojure 1.10.1
```
...we are ready to go!

If we look at the `deps.edn` file again, we can see that _"development/src"_ was already added to the path:
```
 :aliases  {:dev {:extra-paths ["development/src"]
```

This gives us access to the `developent/src` directory (if we activeate the `dev` profile) so that we can work 
with the code. Right now there is only one directory here, but every time we create a new component or base,
they will most likely end up here too.


The "development/src" path belongs to the `dev` alias which we activated previously and also added to the REPL
by selecting the aliases "dev,test".
This means that we have configured everything that [tools.deps](https://github.com/clojure/tools.deps.alpha)
needs and that we are ready to write some Clojure code!

To do that we first need to create a namespace. We suggest that you use `dev` as a top namespace here and not 
the workspace top namespace (e.g. `se.example`).
The reason is that we don't want to mix the code we put here with production code.

One way of structuring the code is to give every developer their own namespace under the `dev` top namespace.
Let's follow that pattern and create the namespace `dev.lisa`.

Right click on the `development/src` directory and select `New > Clojure Namespace` and type "dev.lisa":<br>
<img src="images/new-namespace.png" width="30%" alt="New local REPL">

Now let's write some code:
```clojure
(ns dev.lisa)

(+ 1 2 3)
```
If we send `(+ 1 2 3)` to the REPL we should get `6` back, and if we do,
it means that we now have a working development environment!

# Component

Now when we have a working development environment, let's continue by creating our first component:
```sh
cd example
poly create c name:user
```

<img src="images/component.png" width="20%" alt="Component">

Our workspace will now look like this:
```sh
example
  bases
  components
    user
      resources
        user
          .keep
      src
        se/example/user/interface.clj
      test
        se/example/user/interface_test.clj
  development
    src
      dev/lisa.clj
  environments
  deps.edn
  logo.png
  readme.md
```

The `.keep` file is put there to prevent git to remove `components/user/resources/user`
(empty directories are pruned by git).

The command also printed out this message:
```
  Remember to add src, resources and test directories to 'deps.edn' files.
```

This was a reminder to add source directories to `deps.edn`.
If we don't, then tools.deps and the development environment will not recognise our newly created component,
which would be a pity!

Let's continue by adding the component's `src`, `resources` and `test` directory to `deps.edn`:
```clojure
 :aliases  {:dev {:extra-paths ["development/src"
                                "components/user/src"
                                "components/user/resources"]
  ...
            :test {:extra-paths ["components/user/test"]}
```

After that, we may need to refresh our IDE, by e.g. clicking this link (or the icon we used before):<br>
<img src="images/refresh-ws.png" width="40%" alt="New local REPL">

Now execute the `info` command:<br>
```sh
poly info
```
<img src="images/info-01.png" width="30%" alt="New local REPL">

This tells us that we have one `development` environment, one `user` component and
one `user`interface (and more that we will soon explain).

If you need to adjust the colors, then visit the ****color section*****.

Now, let's add the `core` namespace to `user`:<br>
<img src="images/ide-ws-01.png" width="30%" alt="New local REPL">

...and change it to:
```clojure
(ns se.example.user.core)

(defn hello [name]
  (str "Hello " name "!"))
```

...and update the `interface` to:
```clojure
(ns se.example.user.interface
  (:require [se.example.user.core :as core]))

(defn hello [name]
  (core/hello name))
``` 
Here we delegate the incoming call to the implementing `core` namespace,
which is the recommended way of structuring the code within a `component`. 

## Interface

When we created the `user` component, the `user` interface was also created.

<img src="images/component-interface.png" width="20%" alt="Interface">

So what is an `interface` and what is it good for?

An interface in the Polylith world is a namespace named `interface` that often lives in one but sometimes several
namespaces. It defines a number of `def`, `defn` and `defmacro` statements which forms the contract that 
it exposes to other components and bases.

If more than one component uses the same interface, then all these components must define the exact same set of 
`def`, `defn` and `defmacro` definitions, which is something the tool will help us with.

To have just a single `interface` namespace in a component is often what we want, but it is also possible to 
divide the interface into several namespaces.
To do so we first create an `interface` package (directory) with the name `interface` at the root
and then we put the sub namaspaces in there.

You can find an example where the Polylith tool itself does that, by dividing its 
[util](https://github.com/tengstrand/polylith/tree/master/components/util/src/polylith/clj/core/util/interface)
interface into several sub namespaces:
```sh
util
  interface
    color.clj
    exception.clj
    os.clj
    str.clj
    time.clj
```

This can be handy if you want to group the functions and not put everyone into one place.
If you feel the need to split up the interface, it could also be a signal that it's time to 
split the component into several components!

Code that uses an interface like this might look something like this:
```clojure
(ns dev.lisa
  (:require [se.example.util.interface.time :as time-util]))

(time-util/current-time)
```

We almost forgot to tell _why_ interfaces are so great:
- _Single point of access_. Components can only be accessed through their interface, which makes them
   easier to use and reason about.
- _Encapsulation_. All the implementing namespaces can be changed without breaking the contract.
- _Replacability_. A component can be replaced with another component as long as they use the same interface.

Now let's continue with the next type of building blocks, _bases_.

## Base

A `base` is similar to a `component` except for two things:
- It doesn't have an `interface`.
- It exposes a public API to the outside world.

<img src="images/base.png" width="30%" alt="Base">

The lack of an `interface` makes bases less composable compared to components. But that is not a problem,
because they solve a different problem and that is to be a bridge between the real world and our components.
It solves that by converting input from the outside world that it delegates to different components.

Let's create the `cli` base to see how it works:
```sh
poly create b name:cli
```

Our workspace should now look something like this:
```sh
example
  bases
    cli
      resources
        cli
      src
        se/example/cli/core.clj
      test
        se/example/cli/core-test.clj
  components
    user
      resources
        user
      src
        se/example/user/interface.clj
      test
        se/example/user/interface_test.clj
  development
    src
      dev/lisa.clj
  environments
  deps.edn
  logo.png
  readme.md
```

Now we need to update `deps.edn` with our newly created base (don't forget to refresh the environment afterwards):
```clojure
 :aliases  {:dev {:extra-paths ["development/src"
                                "components/user/src"
                                "components/user/resources"
                                "bases/cli/src"
                                "bases/cli/resources"]
                  :extra-deps {org.clojure/clojure {:mvn/version "1.10.1"}
                               org.clojure/tools.deps.alpha {:mvn/version "0.8.695"}}}

            :test {:extra-paths ["components/user/test"
                                 "bases/cli/test"]}
```

Let's add some code to the base:
```clojure
(ns se.example.cli.core
  (:require [se.example.user.interface :as user])
  (:gen-class))

(defn -main [& args]
  (println (user/hello (first args))))
```

Here we added the `-main` function that later will be called from the command line.
The `(:gen-class)` statement tells the compiler to generate a callable Java class for us.

The next thing we want to do is to build an artifact that will turn the code into something useful, a command line tool.
To do that, we need to start by creating an environment.

## Environment

There are two kind of environments.

<img src="images/environment.png" width="30%" alt="Environment">

1. The `development` environment.
   - This is where we work with the code, often via a REPL.
   - Any extra code, that is not part of a component or base, lives under the `development` folder.
   - What libraries, components and bases that are included is specified in `deps.edn` at the root.
2. Environments that are used to build deployable artifacts.
   - These environments live under the `environments` directory where each environment has its own directory.
   - Each environment has a `deps.edn` config file that specifies which libraries, component and 
     bases that are included.
   - If it has any tests of its own, they will live in the `test` directory under e.g. `environments/my-env`. 
   - Optionally, each environment can also have a `resources` directory. 
   - It's discouraged to have a `src` directory since all production code should only live in components and bases.
   - Here are some examples of artefacts that can be built out of an environment: lambda functions, REST API's,
     libraries, and command line tools.

Let's create an environment:
```sh
poly create e name:command-line
```
 
Our workspace should now look like this:
```sh
example
  bases
    cli
      resources
        cli
      src
        se/example/cli/core.clj
      test
        se/example/cli/core-test.clj
  components
    user
      resources
        user
      src
        se/example/user/interface.clj
      test
        se/example/user/interface_test.clj
  development
    src
      dev/lisa.clj
  environments
    command-line
      deps.edn
  deps.edn
  logo.png
  readme.md
```
 
The tool also reminded us of this:
```sh
  It's recommended to add an alias to :env->alias in deps.edn for the command-line environment.
```

...so let's do that:
```clojure
{:polylith {:vcs "git"
            ...
            :env->alias {"development" "dev"
                         "command-line" "cl"}
```

Now add `user` and `cli` to `deps.edn` in `environments/command-line`:
```clojure
{:paths ["../../components/user/src"
         "../../components/user/resources"
         "../../bases/cli/src"
         "../../bases/cli/resources"]

 :deps {org.clojure/clojure {:mvn/version "1.10.1"}
        org.clojure/tools.deps.alpha {:mvn/version "0.8.695"}}

 :aliases {:test {:extra-paths ["../../components/user/test"
                                "../../bases/cli/test"]
                  :extra-deps  {}}}}
```

Note three things here:
- We didn't add "development/src. 
- The src paths and the test paths are configured at different levels, `:paths` and `extra-paths`.
- All paths begin with "../../".

The reson we didn't add "development/src" is because it contains code that should only be used
from the development environment.

The tool uses the standard way of configuring projects in tools.deps for the "deployable" environments,
but put things under the `:dev` alias for the `development` environment to get more control of what
paths that are included.

The reason all paths begin with "../../" is that `components` and `bases` live two levels up 
compared to `environments/command-line` and not at the root as with the `development` environment.

Now we have created our first environment and it's time to see if we can build an artifact out of it!

## Build

This tool doesn't include any `build` command. To build an artifact out of an environment, we should instead 
use scripts and maybe a build tool. We think they will do a better job and give us the level of control, 
flexibility and power we want.

Let's say we want to create an executable jar file out of the `command-line` environment.
First, we can create a `scripts`directory at the workspace root and copy this [build-uberjar.sh](https://github.com/tengstrand/polylith/blob/master/scripts/build-uberjar.sh)
to it:
```sh
example
  scripts
    build-uberjar.sh
```

Also add `build-cli-uberjar.sh` to the `scripts` directory with this content:
```sh
#!/usr/bin/env bash
./build-uberjar.sh command-line se.example.cli.core
```

...and make sure both are executable:
```sh
chmod +x scripts/build-uberjar.sh
chmod +x scripts/build-cli-uberjar.sh
```

Now add the `aot` and `uberjar` aliases to `eps.edn` in `environments/command-line`:
```clojure

{:paths ["../../components/user/src"
         "../../components/user/resources"
         "../../bases/cli/src"
         "../../bases/cli/resources"]

 :deps {org.clojure/clojure {:mvn/version "1.10.1"}
        org.clojure/tools.deps.alpha {:mvn/version "0.8.695"}}

 :aliases {:test {:extra-paths ["../../components/user/test"
                                "../../bases/cli/test"]
                  :extra-deps  {}}

           :aot     {:extra-paths ["classes"]
                     :main-opts   ["-e" "(compile,'se.example.cli.core)"]}

           :uberjar {:extra-deps {uberdeps {:mvn/version "0.1.10"}}
                     :main-opts  ["-m" "uberdeps.uberjar"]}}}
```

The `aot` alias points to the `se.example.cli.core` namespace, which is where our `-main` function lives.
The `uberjar` alias is used to create a callable uberjar (read more about uberjars [here](https://github.com/tonsky/uberdeps)).

Let's try to build the `command-line` tool:
```sh
cd scripts
./build-cli-uberjar.sh
```

The end of the output should say something like:
```
[uberdeps] Packaged ./target/command-line.jar in 3052 ms
Uberjar created.
```

Let's execute it:
```sh
cd ../environments/command-line/target
java -jar command-line.jar Lisa  
```

```
Hello Lisa!
```

It worked! 

Now it's time to learn more about change management.

## Git

We have already used the `info` command a couple of times without explaining everything in its output.

Let's execute the `info` command again to see the current state of the workspace:<br>
<img src="images/info-02.png" width="30%" alt="Dev alias">

At the top we have the line `stable since: c91fdad`. 
To explain what this is, let's take it from the beginning.

When a Polylith workspace is created, these `git` commands are executed:
```
git init
git add .
git commit -m "Workspace created."
``` 

If we run `git log` from the workspace root, it returns something similar to this:
```sh
commit c91fdad4a34927d9aacfe4b04ea2f304f3303282 (HEAD -> master)
Author: tengstrand <joakimtengstrand@gmail.com>
Date:   Thu Sep 3 06:11:23 2020 +0200

    Workspace created.
```

This is the first and so far only commit of this repository.
This is also the first `stable point in time` of this workspace which the tool uses when it calculates what changes have
been made (up till now). Notice that the first letters of the hash corresponds to `stable since: c91fdad`
and this is because it refers to this SHA-1 hash in git.
 
The `command-line` and `development` environment and the `user` and `cli` brick (components and bases are
also called `bricks`) are all marked with an asterisk, `*`. The way the tool calculates changes is to ask
`git` by running this command internally:
```sh
git diff c91fdad4a34927d9aacfe4b04ea2f304f3303282 --name-only
```

We can also run the `diff` command, which will execute the same git statement internally:
```clojure
poly diff
```

The output is the same:
```
bases/cli/resources/cli/.keep
bases/cli/src/se/example/cli/core.clj
bases/cli/test/se/example/cli/core_test.clj
components/user/resources/user/.keep
components/user/src/se/example/user/core.clj
components/user/src/se/example/user/interface.clj
components/user/test/se/example/user/interface_test.clj
deps.edn
development/src/dev/lisa.clj
environments/command-line/deps.edn
scripts/build-cli-uberjar.sh
scripts/build-uberjar.sh
```

Here we have the answer to were the `*` signs came from. The paths that starts with `environments/command-line`, 
`development`, `components/user` and `bases/cli` makes the tool understand that `command-line`, `development`,
`user` and `cli` are changed.

When we created the workspace, a [.gitignore](https://git-scm.com/docs/gitignore) file was also created for us.
Now it's a good time to add more rows here if needed. Right now it looks like this:
```sh
**/classes
**/target
```

Let's add and commit the changed files:
```
git add --all
git commit -m "Created the user and cli bricks."
```

Let's have a look at our workspace repository again:
```sh
git log --pretty=oneline
```

Output:
```sh
e7ebe683a775ec28b7c2b5d77e01e79d48149d13 (HEAD -> master) Created the user and cli bricks.
c91fdad4a34927d9aacfe4b04ea2f304f3303282 Workspace created.
```

If we run the `info` command again, it will return the same result as before, and the reason is that we
haven't told git to move the `stable point in time` to our second commit.

# Tagging

The way we mark a `stable point in time` is to tag it with git (-f tells git to reuse the tag if already exists):
```sh
git tag -f stable-lisa
```

If we now run `git log --pretty=oneline` again:
```sh
e7ebe683a775ec28b7c2b5d77e01e79d48149d13 (HEAD -> master, tag: stable-lisa) Created the user and cli bricks.
c91fdad4a34927d9aacfe4b04ea2f304f3303282 Workspace created.
```

...we can see that the second commit has been tagged with `stable-lisa`.
 
If we run the `info` command again:

<img src="images/info-03.png" width="30%" alt="Stable Lisa">

...the `stable since` hash has been updated and is now tagged with `stable-lisa`.
All the `*` signs are gone because no `component`, `base` or `environment` 
has yet changed since the second commit (which can be verified by running `poly diff` again).

We added the tag `stable-lisa` but we could have named the tag with anything that starts with `stable-`.
We choose `stable-lisa` because Lisa is our name (let's pretend that at least!). The idea is that every developer could use
their own unique tag name that doesn't conflict with other developers. The CI build should also use its own patten,
like `stable-` plus the build number, to mark successful builds.

The pattern is configured in `deps.edn` and can be changed if you prefer another pattern:
```clojure
            :stable-since-tag-pattern "stable-*"
```

The way the tool finds the latest tag is to run this command internally:
```
git tag --sort=committerdate -l 'stable-*'
``` 

Then it uses the last line of the output, or if no match was found, the first commit in the repository.

## Flags

We have one more thing to cover regarding the `info` command, and that is what the `x` and `-` flags mean:
<img src="images/environment-flags.png" width="25%" alt="Flags">

Each flag under _sources_ has a different meaning:<br>
<img src="images/flags.png" width="30%" alt="Flags">

The `---` for the `command-line` environment means we have a `environments/command-line`
directory with no `src` or `test` directories in it and that no tests will be executed for this environment.

The `x--` for the `development` environment means we have a `development/src` directory
but no `development/test` directory and that no tests will be executed for this environment.

Let's have a look at the second section:<br>
<img src="images/brick-flags.png" width="25%" alt="Flags">

Here the flags have a slightly different meaning:<br>
<img src="images/flags-included.png" width="38%" alt="Flags">

The `xx-` for the `user` component tells that both `components/user/src` and `components/user/test` 
are included in the `command-line` and `development` environments and that no brick tests will be executed.

The `xx-` for the `cli` base follows the same pattern as for the `user` component but for the
`bases/cli` directory.

The `command-line` environment is configured in`environments/command-line/deps.edn`:
```clojure
{:paths ["../../components/user/src"
         "../../bases/cli/src"
         "../../bases/cli/resources"]

 ...
 :aliases {:test {:extra-paths ["../../components/user/test"
```

The `development` environment is configured in `./deps.edn`:
```clojure
 :aliases  {:dev {:extra-paths [...
                                "components/user/src"
  ...
            :test {:extra-paths ["components/user/test"
```

 If we execute `poly info src:r` (or the longer `poly info src:resources`):<br>
<img src="images/info-04.png" width="30%" alt="Status resources">


The `resources` directory is not inserted into the second position:<br>
<img src="images/flags-resources.png" width="20%" alt="Status resources">

## Test

Nothing is marked to be tested at the moment. But if we change the `core` namespace in the `user` component
by adding an extra `!`, that should do the trick:
```clojure
(ns se.example.user.core)

(defn hello [name]
  (str "Hello " name "!!"))
```

We can verify that the tool recognises the change by running the `diff` command:
```
components/user/src/se/example/user/core.clj
```

...and if we run the `info` command again:<br>
<img src="images/info-05.png" width="30%" alt="Status resources">

...the `user` component is now marked with an asterisk, `*`. If we look carefully we will also notice that 
the status flags `xxx` under the `cl` column now have an `x` in the last position. As we already know, 
this means that the tests for `user` and `cli` will be executed from the `command-line` environment
if we execute the `test` command.

But why is `cli` marked to be tested? The answer to that question is that even though it itself hasn't
change, it depends on something that has, which is the `user` component.
  
The columns under the `development` environment are all marked as `xx-`. The reason the `development`
environment is not marked to be tested is that the `development` environment's tests are excluded by default.

But before we run the test command, we should first add a test by editing the `interface-test` 
namespace in the `user` component:
```clojure
(ns se.example.user.interface-test
  (:require [clojure.test :refer :all]
            [se.example.user.interface :as user]))

(deftest hello--when-called-with-a-name--then-return-hello-phrase
  (is (= "Hello Lisa!"
         (user/hello "Lisa"))))
```

Now let's run the `test` command:
```sh
poly test
```

Output:
```
Runing tests for the command-line environment, including 2 bricks: user, cli

Testing se.example.cli.core-test

Ran 0 tests containing 0 assertions.
0 failures, 0 errors.

Test results: 0 passes, 0 failures, 0 errors.

Testing se.example.user.interface-test

FAIL in (hello--when-called-with-a-name--then-return-hello-phrase) (interface_test.clj:6)
expected: (= "Hello Lisa!" (user/hello "Lisa"))
  actual: (not (= "Hello Lisa!" "Hello Lisa!!"))

Ran 1 tests containing 1 assertions.
1 failures, 0 errors.
```


OOps, the test failed! Remember that we added an extra `!` so now we need to update the test accordingly:
```clojure
(ns se.example.user.interface-test
  (:require [clojure.test :refer :all]
            [se.example.user.interface :as user]))

(deftest hello--when-called-with-a-name--then-return-hello-phrase
  (is (= "Hello Lisa!!"
         (user/hello "Lisa"))))
```

If we run the `test` command again, it will now pass:
```
Runing tests for the command-line environment, including 2 bricks: user, cli

Testing se.example.cli.core-test

Ran 0 tests containing 0 assertions.
0 failures, 0 errors.

Test results: 0 passes, 0 failures, 0 errors.

Testing se.example.user.interface-test

Ran 1 tests containing 1 assertions.
0 failures, 0 errors.

Test results: 1 passes, 0 failures, 0 errors.
Execution time: 1 seconds
```


We have already mentioned that the `development` environment is not included when we run
the `test` command.
Instead, we will run the tests from the `development` environment using the IDE, as part of our normal
workflow, by e.g. sending them to the REPL. Only when we want that extra check, we'll execute 
the `test` command, e.g. before we commit something.

If we still want include the development tests, then we can do that byt passing in `:dev` or `env:dev`.
Let's do that with the `info`:
```sh
poly info :dev
```

Output:<br>
<img src="images/info-06.png" width="30%" alt="Test dev">

And yes, now the tests for the `development` environment are also included. When we give an environment 
using `env` (`:dev` is a shortcut for `env:dev`) only that environment will be included. 
One way to test both the `development` environment and the `command-line` environment is to 
colon separate them after `env`:
```
poly info env:cl:dev
```
<img src="images/info-07.png" width="30%" alt="Test command-line and dev">

Now both the `development` and the `command-line` environment is marked for test execution.
Here we used the environment aliases `cl` and `dev` but we could also have passed in the environment 
names or a mix of the two, e.g. `poly info env:command-line:dev`.  
 
## Environment tests

Before we execute any tests, let's add an environment test for the `command-line` environment.
Start by 

### Colors

To make things more colourful create the `~/.polylith/config.edn` config file under your `USER-HOME` directory
with the following content:
```
{:color-mode "bright"
 :thousand-separator ","
 :empty-character "·"}
```
- The _color-mode_ can be set to either "none", "light" or "dark", depending on the colour schema you use.
  The only difference between "light" and "dark" is that they use different [codes](https://github.com/tengstrand/polylith/blob/core/components/util/src/polylith/clj/core/util/colorizer.clj) for grey.
- The _thousand-spearator_ is used to separate numbers larger then 999 like 12,345.
- The _empty-character_ can be replaced by a . (period) if your computer has problems showing it (they are used in the `deps` command).

If we run the `info` command again:
```sh
clj -A:poly info
```
<img src="images/polylith-info-bright.png" width="40%" alt="Polylith workspace">

The diagram is now shown with colors! Let's improve the readability by switching to dark mode:

```
{:color-mode "dark"
 :thousand-separator ","
 :empty-character "·"}
```
<img src="images/polylith-info.png" width="40%" alt="Polylith workspace">

That's better! 

If you want to use the same colors in your terminal, here they are:<br>
<img src="images/polylith-colors.png" width="50%" alt="Polylith colors">

If the colors (f8eeb6, bfefc5, 77bcfc, e2aeff, cccccc, 24272b, ee9b9a) looks familiar to you, it's because they are 
more or less stolen from the [Borealis](https://github.com/Misophistful/borealis-cursive-theme) colour schema!

------------------

It's true that the default name of an `interface` is "interface", but this can be changed by editing `:interface-ns` 
in `deps.edn` to something else. Only do that if you have really good reasons to do so).

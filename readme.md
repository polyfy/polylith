# <img src="logo.png" width="50%" alt="Polylith" id="logo">
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

The workspace directory is the top-level container for all our code and contains everything we need
work with Polylith systems.

Let’s start by creating the _example_ workspace with the top namespace _se.example_:
```sh
poly create w name:example top-ns:se.example
``` 

The workspace directory structure will end up like this:
```sh
example           # workspace root dir
  bases           # empty dir
  components      # empty dir
  development
    src
  environments    # empty dir
  deps.edn        # the workspace config file (tools.deps)
  logo.png        # the polylith logo
  readme.md       # documentation
```

This directory structure helps us work with, and reason about the code and the higher level structure. 
Each top directory is responsible for its own part of a Polylith system.
A `base` exposes a public API. A `component` is responsible for a specific domain 
or part of the system. 
An `environment` specifies our deployable artifacts and what components and bases they contain.
Finally, we have the `development` environment that we use when we work with the code.
 
`deps.edn` contains some workspace settings and hosts the `development` environment:

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

This gives us access to the `developent/src` directory so that we can work with the code. 
Right now there is only one directory here, but every time we create a new component or base,
they will most likely end up here too so that we can work with all the new code.


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

The command also printed out this message:
```
  Remember to add src, resources and test directories to 'deps.edn' files.
```

This was a reminder to add source directories to `deps.edn`.
If we don't, then tools.deps and the development environment will not recognise our newly created component,
which would be a little bit sad!

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
one `user`interface. Let's leave the other information for later.

If you need to adjust the colors, then visit the ****color section*****.

Now, let's add the `core` namespace to `user`:<br>
<img src="images/ide-ws-01.png" width="30%" alt="New local REPL">

And change it to:
```clojure
(ns se.example.user.core)

(defn hello [name]
  (str "Hello " name "!"))
```

And update the `interface` to:
```clojure
(ns se.example.user.interface
  (:require [se.example.user.core :as core]))

(defn hello [name]
  (core/hello name))
``` 
Here we delegate the incoming call to the implementing `core` namespace,
which is the recommended way of structuring the code within a `component` by delegating from the `interface`
namespace to implementing namespaces. But wait. Something is missing. A test!

Let's edit `interface-test`:
```clojure
(ns se.example.user.interface-test
  (:require [clojure.test :refer :all]
            [se.example.user.interface :as user]))

(deftest hello--when-called-with-a-name--then-return-hello-phrase
  (is (= "Hello Lisa!"
         (user/hello "Lisa"))))
```

The test is green and we have created our first component!

## Interface

When we created the `user` component, the `user` interface was also created.

<img src="images/component-interface.png" width="20%" alt="Interface">

So what is an interface and what is it good for?

An interface in the Polylith world is a namespace named `interface` that often lives in one but sometimes several
components. It defines a number of `def`, `defn` and `defmacro` statements which forms the contract that 
it exposes to other components and bases.

If more than one component uses the same interface, then all these components must define the exact same set of 
`def`, `defn` and `defmacro` definitions. This is something the tool will help us with.

To have just a single interface namespace in a component is often what we want, but it is also possible to 
divide the interface into several namespaces.
To do that we first create an `interface` package (directory) with the name `interface` at the root
and then we put the sub namaspaces in there.

You can find an example where the Polylith tool itself does that, by dividing its 
[util](https://github.com/tengstrand/polylith/blob/master/components/util/src/polylith/clj/core/util/interface)
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

This can be handy if you want to group the functions and not put everyone in one place.
If you feel the need to split up the interface, it could also be a signal that it's time to 
split the component into several components!

Code that uses an interface like this might look something like this:
```clojure
(ns dev.lisa
  (:require [se.example.util.interface.time :as time]))

(time/current-time)
```

There are several reasons why we have interfaces:
- Single point of access. Components can only be accessed through their interface, which makes them easier to use and reason about.
- Encapsulation. All the implementing namespaces can be changed without breaking the contract.
- Replacability. A component can be replaced with another component as long as they use the same interface.

## Base

A `base` is similar to a `component` except for two things:
- It doesn't have an `interface`.
- It exposes a public API to the outside world.

<img src="images/base.png" width="30%" alt="Base">

The lack of an `interface` makes bases less composable compared to components. That is not a big problem,
because they solve a different problem and that is to convert input from the outside world and delegate 
it to different components via their interfaces.

Let's create the `cli` base to see how that works:
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
        se/example/cli/api.clj
      test
        se/example/cli/api-test.clj
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

We also need to update `deps.edn` with our newly created base:
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

Now, let's add some code to the our base:
```clojure
(ns se.example.cli.api
  (:require [se.example.user.interface :as user])
  (:gen-class))

(defn -main [& args]
  (println (user/hello (first args))))
```

Here we added the `-main` function that later will be called from the command line.
The `(:gen-class)` statement tells the compiler that we want a Java class to be generated.

The next natural step is to build an artifact that we can use as a command line tool. 
To do that, we need to create an environment.

## Environment

There are two kinds of environments:
1. The `development` environment, which is used to work with the code, often via a REPL. 
   All the included paths are specified in `deps.edn` at the root, like `development/src`,
   `components/user/src` and `bases/cli/src`.
2. Other environments are used to build deployable artifacts like libraries, lambda functions, REST API's and command line tools.
   Each environment has its own directory under `environments` with a `deps.edn` file
   that specifies all the component and base paths that it includes. 
   If it has any tests of its own, they will live in the `test` directory. 
   Optionally it can also have a `src` and `resources` directory.

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
        se/example/cli/api.clj
      test
        se/example/cli/api-test.clj
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
 
The command also printed out this message:
```sh
  It's recommended to add an alias to :env->alias in deps.edn for the command-line environment.
```

So let's do that:
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

The reson this environment is configured in a differnt way compared to the development environment is that
it is more static in its nature with less need to switch between different configurations (aliases).
All the paths will be included by default without the need to specify an alias.

And finally, the reason all paths begin with "../../" is that `components` and `bases` live two levels up 
compared to `environments/command-line` and not at the root as with the `development` environment.

## Build

The tool doesn't contain any build command and the reason is that this can be done quite easily by using scripts
which hopefully will give us the level of control, flexibility and power we want.

Let's say we want to create an executable jar file out of the `command-line` environment.
First, we can create a `scripts`directory at the workspace root and copy this [build-uberjar.sh](https://github.com/tengstrand/polylith/blob/core/scripts/build-uberjar.sh)
to it:
```sh
example
  scripts
    build-uberjar.sh
```

Also add `build-cli-uberjar.sh` to the `scripts` directory with this content:
```sh
#!/usr/bin/env bash
./build-uberjar.sh command-line
```

And make sure they are both executable:
```sh
chmod +x build-uberjar.sh
chmod +x build-cli-uberjar.sh
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
                     :main-opts   ["-e" "(compile,'se.example.cli.api)"]}

           :uberjar {:extra-deps {uberdeps {:mvn/version "0.1.10"}}
                     :main-opts  ["-m" "uberdeps.uberjar"]}}}
```

The `aot` alias points to the `se.example.cli.api` namespace, which is where our `-main` function lives.
The `uberjar` alias is used to create a callable uberjar.

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

Now we can try to execute our command line tool:
```sh
cd ../environments/command-line/target
java -jar command-line.jar Lisa  
```

```
Hello Lisa!
```

Wow, it worked!

## Git

We have already used the `info` command a couple of times without explaining everything in its output.
Let's execute it again to see the current state of the workspace:

<img src="images/info-02.png" width="30%" alt="Dev alias">

At the top we have the line `stable since: c91fdad`. 
To explain what this is, let's take it from the beginning.

When a Polylith workspace is created, these `git` commands are executed:
```
git init
git add .
git commit -m "Initial commit."
``` 

If we run `git log` from the workspace root, it returns this:
```sh
commit c91fdad4a34927d9aacfe4b04ea2f304f3303282 (HEAD -> master)
Author: tengstrand <joakimtengstrand@gmail.com>
Date:   Thu Sep 3 06:11:23 2020 +0200

    Initial commit.
```

This is the first and so far only commit of this repository.
This is also the first `stable point in time` which the tool uses when it calculates what changes have
been made, up till now. Notice that the first letters of the hash corresponds to `stable since: c91fdad`
and this is because it refers to this SHA-1 hash in git.
 
The `command-line` and `development` environment and the `user` and `cli` brick (components and bases are
also called `bricks`) are all marked with an asterisk, `*`. The way the tool calculates changes is to ask
`git` by running this command internally:
```sh
git diff c91fdad4a34927d9aacfe4b04ea2f304f3303282 --name-only
```

Which is also what the `diff` command does for us:
```clojure
poly diff
```

The output is the same:
```
bases/cli/resources/cli/.keep
bases/cli/src/se/example/cli/api.clj
bases/cli/test/se/example/cli/api_test.clj
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

Here we have the answer to were the `*` signs come from, that the paths starting  with  `environments/command-line`, 
`development`, `components/user` and `bases/cli` have changed since the first commit.

When we created the workspace, a [.gitignore](https://git-scm.com/docs/gitignore) file was also created for us.
Now it's a good time to add more rows here if needed:
```sh
**/classes
**/target
```

Let's add and commit the files:
```
git add --all
git commit -m "Second commit."
```

Let's have a look at our workspace repository again:
```sh
git log --pretty=oneline
```

Output:
```sh
e7ebe683a775ec28b7c2b5d77e01e79d48149d13 (HEAD -> master) Second commit.
c91fdad4a34927d9aacfe4b04ea2f304f3303282 Initial commit.
```

If we run the `info` command again, it would return the same result as before, and the reason is that we
haven't told git that the `stable point in time` has moved to our second commit.

To do so, we can run this command:
```sh
git tag -f stable-lisa
```

If we now run `git log --pretty=oneline` again:
```sh
e7ebe683a775ec28b7c2b5d77e01e79d48149d13 (HEAD -> master, tag: stable-lisa) Second commit.
c91fdad4a34927d9aacfe4b04ea2f304f3303282 Initial commit.
```

We can see that the second commit has been tagged with `stable-lisa` and if we run the `info` command again:

<img src="images/info-03.png" width="30%" alt="Stable Lisa">

We can see that the `stable since` hash has changed and is tagged with `stable-lisa`.
All the `*` signs are gone because no `component`, `base` or `environment` 
has yet changed since the second commit.

We added the tag `stable-lisa` but we could have named the tag anything that starts with `stable-`.
We choose `stable-lisa` because Lisa is our name (let's pretend that at least!). The idea is that every developer could use
their own unique tag name that doesn't conflict with other developers. The CI build should also use its own patten,
like `stable-` plus the build number to mark successful builds.

The pattern is configured in `deps.edn` and can be changed if you prefer another pattern:
```clojure
            :stable-since-tag-pattern "stable-*"
```

The way the tool finds the latest tag is to run this command:
```
git tag --sort=committerdate -l 'stable-*'
``` 

And take the last one, or if no tag was found, the first commit in the repository.

## Source flags

We have one more thing to cover regarding the `info` command, and that is what the `x` and `-` signs mean:
<img src="images/environment-flags.png" width="25%" alt="Flags">

The first sign of a group of three, says whether the environment has a `src` directory or not,
and the second whether it has a `test` directory or not.

The `x--` for the `development` enviroment has an `x` marked in its first position and a `-` in its second,
which means we have a `development/src` directory but not a `development/test` directory in our workspace.

The `command-line    cl      ---` row says that we have a `environments/command-line` directory
in our workspace, but that it doesn't contain a `src` or `test` directory.

Let's have a look at the second section:<br>
<img src="images/brick-flags.png" width="25%" alt="Flags">

The flags here tells whether a brick's `src` or `test` directory is part of an environment or not
(if the path is added to the enviroment's `deps.edn`).

The `xx-` for the `user` row, column `cl`, tells that both the `src` and the `test` directory for the `user`
component has been added to the `command-line` environment, file `environments/command-line/deps.edn`:
```clojure
{:paths ["../../components/user/src"
 ...
 :aliases {:test {:extra-paths ["../../components/user/test"
```

The `xx-` for the `user` row, column `dev`, tells that both the `src` and the `test` directory for the `user`
component has been added to the `development` environment, file `deps.edn` (at the root):
```clojure
 :aliases  {:dev {:extra-paths [...
                                "components/user/src"
  ...
            :test {:extra-paths ["components/user/test"
```

The `xx-` in the `cli` row follows the same pattern and says that both the `src` and the `test` directories
are inluded in both the `command-line` and the `development` environments.


If we type `poly info src:resources` or the shorter form `poly info src:r`:<br>
<img src="images/info-04.png" width="30%" alt="Status resources">

A fourth flag is now inserted into the second position, telling if there is a `resources` directory or not. 
The first position is still `src` but `test` now lives in the third position.

## Test

We didn't explain what the last flag was. 
Let's edit the `core.clj` namespace in the `user` component and add one more ! sign:
```clojure
(ns se.example.user.core)

(defn hello [name]
  (str "Hello " name "!!"))
```

We can check that the tool has recognised the change by running the `diff` command:
```
components/user/src/se/example/user/core.clj
```

If we run the `info` command again:<br>
<img src="images/info-05.png" width="30%" alt="Status resources">

The `user` component is now marked as changed with the `*` sign. If we look carefully we may notice that the 
status flags `xxx` under the `cl` column now has an `x` in the last position.
This means that the component's tests is `market to be executed` together with the `command-line` environment.

The `cli` base under the `cl` column also has its last flag marked with an `x`.
The reason is that the tool has recognised that it uses the changed `user` component,
which is the reason it marks it `to be tested` together with the `command-line` environment.

But why isn't the last flag under the `dev` column marked with an `x`?
The reason is that the tests in the `development` environment are not executed by default when running the `test`
command:
```sh
poly test
```

Output:
```
Runing tests for the command-line environment, including 2 bricks: user, cli

Testing se.example.cli.api-test

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


OOps, we forgot to update our test! Let's do that:
```clojure
(ns se.example.user.interface-test
  (:require [clojure.test :refer :all]
            [se.example.user.interface :as user]))

(deftest hello--when-called-with-a-name--then-return-hello-phrase
  (is (= "Hello Lisa!!"
         (user/hello "Lisa"))))
```




There is a way to include the tests for the `developmemnt` environment and that is to 


### Colours

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

The diagram is now shown with colours! Let's improve the readability by switching to dark mode:

```
{:color-mode "dark"
 :thousand-separator ","
 :empty-character "·"}
```
<img src="images/polylith-info.png" width="40%" alt="Polylith workspace">

That's better! 

If you want to use the same colours in your terminal, here they are:<br>
<img src="images/polylith-colors.png" width="50%" alt="Polylith colors">

If the colours (f8eeb6, bfefc5, 77bcfc, e2aeff, cccccc, 24272b, ee9b9a) looks familiar to you, it's because they are 
more or less stolen from the [Borealis](https://github.com/Misophistful/borealis-cursive-theme) colour schema!

------------------

It's true that the default name of an `interface` is "interface", but this can be changed by editing `:interface-ns` 
in `deps.edn` to something else. Only do that if you have really good reasons to do so).

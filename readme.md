# <img src="images/logo.png" width="50%" alt="Polylith" id="logo">
An open source tool used to develop Polylith based architectures in Clojure.

---------

Welcome to the wonderful world of Polylith!

This tool is made by developers for developers with the goal to maximise productivity and 
increase the quality of the systems we write. 
It supports your build pipeline, but is not a build tool itself.

The Polylith concept can be implemented in any programming language, 
but this version of the Polylith tool targets [Clojure](https://clojure.org)
which is a powerful and simple functional language for the [JVM](https://www.google.com/url?sa=t&rct=j&q=&esrc=s&source=web&cd=&cad=rja&uact=8&ved=2ahUKEwiB88eLxansAhUyi8MKHd6jDPEQFjAAegQIBRAC&url=https%3A%2F%2Fen.wikipedia.org%2Fwiki%2FJava_virtual_machine&usg=AOvVaw0YtnMyoG7GQIhUPeLulbfr).

Polylith introduces the architectural concept of “service level building blocks”, 
which can be combined like LEGO bricks to build our services and systems. 
Polylith’s LEGO-like bricks are easy to reason about, test, refactor, and reuse. 
They allow us to work with all our code in one place for maximum productivity, using a single
[REPL](https://en.wikipedia.org/wiki/Read%E2%80%93eval%E2%80%93print_loop)

The bricks can easily be put together to form different kinds of deployable artifacts, 
like services, tools and libraries, in the same way we put together LEGO when we were kids! 
Not surprisingly, it's just as simple and fun!

To give you an idea of what that can look like, take a quick look at the bricks and libraries
that we use to build the Polylith tool (which is itself a Polylith workspace):

<img src="images/polylith-info-deps-libs.png" width="100%">

To better understand the principles and ideas behind this tool, we recommend you first read the...

<a href="https://polylith.gitbook.io">
<img src="images/doc.png" width="10%" href="https://polylith.gitbook.io">
<br>...high-level documentation!
</a>

<br>Enjoy the ride!

## Leiningen version

The old [lein-polylith](https://github.com/tengstrand/lein-polylith) tool has reached the 
end of its life and has been replaced by the tools.deps version.
If you have any old Leiningen based projects to migrate, follow the instructions [here](https://github.com/tengstrand/lein-polylith/blob/migrate/migrate/migrate.md).

The biggest difference compared to the old tools is that the new tool is based on
[tools.deps](https://github.com/clojure/tools.deps.alpha) instead of [Leiningen](https://leiningen.org/)
which has a number of benefits, like an improved development experience, faster tests, Windows support,
and more.

## Table of Contents

- [Content](#content)
- [Installation](#installation)
- [Realworld Example](#realworld-example)
- [Workspace](#workspace)
- [Development](#development)
- [Component](#component)
- [Interface](#interface)
- [Base](#base)
- [Project](#project)
- [Tools.deps](#toolsdeps)
- [Build](#build)
- [Git](#git)
- [Tagging](#tagging)
- [Flags](#flags)
- [Testing](#testing)
- [Profile](#profile)
- [Dependencies](#dependencies)
- [Libraries](#libraries)
- [Context](#context)
- [Naming](#naming)
- [Configuration](#configuration)
- [Workspace state](#workspace-state)
- [Git hook](#git-hook)
- [Mix languages](#mix-languages)
- [CI and Deployment](doc/ci-and-deployment.md)
- [Commands](doc/commands.md)
- [Colors](#colors)
- [Contact](#contact)
- [License](#license)

## Content

This documentation aims to be a practical guide to this tool with lots of code examples. 
We encourage you to follow the code examples and try it out yourself. 
We will guide you through the steps of creating a workspace with projects composed of 
components, bases and libraries and how to work with them from the development environment.

We will give a short introduction to tools.deps and how to use build scripts to create
deployable artifacts. We will show how git is used to tag the code
and how it enables us to test and release the code incrementally.

We will show how profiles will help us work from a single development environment
for maximum efficiency and how dependencies and library usage can be displayed.

We will explain the value of components and how they bring context to our development experience, 
which will help us build decoupled and scalable systems from day one.

Happy coding!

## Installation

The Polylith tool can be installed on Mac, Linux or Windows, so please follow the 
installation instructions for your operating system of choice.

### Install on Mac

To use the Polylith tool and to get access to all the features in tools.deps, make sure you have
[CLI tools](https://clojure.org/guides/getting_started)
and [git](https://git-scm.com/book/en/v2/Getting-Started-Installing-Git) installed.
If you install git for the first time, don't forget to set the 
[user](https://docs.github.com/en/github/using-git/setting-your-username-in-git)
name and email.

To install the `poly` command on Mac, execute:
```
brew install polyfy/polylith/poly
``` 

If you get the error "openjdk-13.0.2.jdk could not be opened...", do this:
- Open MacOS "System Preferences > Security & Privacy > General".
- Click Allow at the bottom for "openjdk-13.0.2.jdk".
- Run `brew install polyfy/polylith/poly` again.

Verify the installation by executing `poly help`.

### Install on Linux

To use the Polylith tool and to get access to all the features in tools.deps, make sure you have
[CLI tools](https://clojure.org/guides/getting_started)
and [git](https://git-scm.com/book/en/v2/Getting-Started-Installing-Git) installed.
If you install git for the first time, don't forget to set the 
[user](https://docs.github.com/en/github/using-git/setting-your-username-in-git)
name and email.

To install the `poly` command on Linux:

- Download the [latest release](https://github.com/polyfy/polylith/releases/latest) of the `poly` jar,
  e.g. `poly-0.1.0-alpha9.jar`.
- Create a directory, e.g. `/usr/local/polylith` and copy the jar file to that directory.
- Create a file with the name `poly` and put it in e.g. `/usr/local/bin` with this content:
 ```
#!/bin/sh

ARGS=""
while [ "$1" != "" ] ; do
  ARGS="$ARGS $1"
  shift
done

exec "/usr/bin/java" "-jar" "/usr/local/polylith/poly-0.1.0-alpha9.jar" $ARGS
```
- Make sure that:
  - you point to the correct jar file.
  - the path to `java` is correct (can be verified with `which java`).
- If you choose `/usr/local/bin`, it was probably already on your path, otherwise you have to add it.
- Make it executable by executing `chmod +x poly`.

Verify the installation by executing `poly help`.

### Install on Windows

To use the Polylith tool and to get access to all the features in tools.deps, make sure you have
[CLI tools](https://clojure.org/guides/getting_started)
and [git](https://git-scm.com/book/en/v2/Getting-Started-Installing-Git) installed.
If you install git for the first time, don't forget to set the 
[user](https://docs.github.com/en/github/using-git/setting-your-username-in-git)
name and email.

If you got this error when installing `clj`:
```
clj : The 'clj' command was found in the module 'ClojureTools', but the module could not be loaded.
For more information, run 'Import-Module ClojureTools'.
```

...and if you followed the instruction and executed this:
````
Import-Module ClojureTools
````
...and got this error:
```
Import-Module : File C:\Users\Admin\Documents\WindowsPowerShell\Modules\ClojureTools\ClojureTools.psm1 
cannot be loaded because running scripts is disabled on this system. For more information, 
see about_Execution_Policies at https:/go.microsoft.com/fwlink/?LinkID=135170.
```

...then try this:
```
Set-ExecutionPolicy -Scope Process -ExecutionPolicy Bypass
```

To install the `poly` command on Windows:

- Download the [latest release](https://github.com/polyfy/polylith/releases/latest) of the `poly` jar,
  e.g. `poly-0.1.0-alpha9.jar`.
- Create the `Polylith` directory somewhere on your machine, e.g. 
  `C:\Program Files\Polylith` and copy the jar file to that directory.
- Create the file `poly.bat` with this content (make sure you point to the jar):
```sh
@echo off
start /wait /b java -jar "C:\Program Files\Polylith\poly-0.1.0-alpha9.jar" %*
```
- Add `C:\Program Files\Polylith` to the Windows `PATH` variable.

Test the installation by typing `poly help` from the command line.

> Note: The coloring of text are not supported on Windows.

### Use the Polylith Tool as a dependency

An alternative way of executing the `poly` tool is to specify it as a dependency, by giving a commit SHA.
To use it this way, add one of the following aliases to the `:aliases` section in your `deps.edn`.

#### Via Clojars

```clojure
{
...
 :aliases   {:poly  {:extra-deps {polylith/clj-poly
                                  {:mvn/version "0.1.0-alpha9"}}
                     :main-opts  ["-m" "polylith.clj.core.poly-cli.core"]}}
...
}
```

#### Via GitHub

```clojure
{
...
 :aliases   {:poly  {:extra-deps {polylith/clj-poly
                                  {:git/url   "https://github.com/polyfy/polylith.git"
                                   :sha       "INSERT_LATEST_SHA_HERE"
                                   :deps/root "projects/poly"}}
                     :main-opts  ["-m" "polylith.clj.core.poly-cli.core"]}}
...
}
```

You should replace `INSERT_LATEST_SHA_HERE` with a [commit SHA](https://github.com/polyfy/polylith/commits/master) from this repository.

Once you have added one of the aliases above, you can now use the poly tool from the terminal:

```sh
clj -M:poly info
```

### Add other Polylith artifacts as a dependency
Similarly, you can use other artifacts from this repository, `clj-api` or `clj-poly-migrator` as dependencies. For example, in order to add `clj-api` as a dependency, add one of the following to your `:deps` section in your `deps.edn` file:

```clojure
polylith/clj-api {:mvn/version "0.1.0-alpha9"}
```
or
```clojure
polylith/clj-api {:git/url   "https://github.com/polyfy/polylith.git"
                  :sha       "INSERT_LATEST_SHA_HERE"
                  :deps/root "projects/api"}
```

## RealWorld Example

If you want to start by seeing how a full-blown system looks like in Polylith, then head over to the 
[RealWorld](https://github.com/furkan3ayraktar/clojure-polylith-realworld-example-app) project,
where you can also compare it with [implementations made in other languages](https://github.com/gothinkster/realworld).
Otherwise, let’s jump in and start making our own very basic Polylith project!

## Workspace

The workspace directory is the place where all our code and most of the [configuration](#configuration) lives.

Let’s start by creating the `example` workspace with the top namespace `se.example` by using the [create workspace](#create-workspace) command
(`create w` works as well as `create workspace`):
```sh
poly create workspace name:example top-ns:se.example
``` 

The workspace directory structure will end up like this:
```sh
example            # workspace dir
├── .git           # git repository dir
├── bases          # bases dir
├── components     # components dir
├── deps.edn       # development config file
├── development
│   └── src        # development specific code
├── logo.png       # polylith logo
├── projects       # projects dir
├── readme.md      # documentation
└── workspace.edn  # workspace config file
```

The directory structure is designed for quick navigation and ease of use.
It helps us to understand and find all our service-level building blocks,
which lets us reason about the system at a higher level.

Each top-level directory contains a specific type of Polylith concept. 
A `base` is a building block that exposes a public API to external systems. 
A `component` is a building block for encapsulating a specific domain or part of the system.
A `project` specifies our deployable artifacts and what components, bases, and libraries they contain. 
Finally, we have the `development` project (`development` + `deps.edn`) 
that we use to work with the code in one place.

This structure gives a consistent shape to all Polylith projects, and ensures that both new developers 
and veterans can quickly understand and start working with systems that are new to them. 
We think you will soon be addicted to the power and simplicity the Polylith structure gives to your projects!

The `bases`, `components` and `projects` directories also contain a `.keep` file,  which are added to prevent git 
from deleting these directories, and can be removed as soon as we add something to them.
A workspace is always initialized to use [git](https://git-scm.com/), but more on that later.

The `workspace.edn` file looks like this:
```clojure
{:vcs "git"
 :top-namespace "se.example"
 :interface-ns "interface"
 :default-profile-name "default"
 :compact-views #{}
 :release-tag-pattern "v[0-9]*"
 :stable-tag-pattern "stable-*"
 :projects {"development" {:alias "dev", :test []}}}
```

...and `deps.edn` like this:

```clojure
{:aliases  {:dev {:extra-paths ["development/src"]
                  :extra-deps {org.clojure/clojure {:mvn/version "1.10.1"}
                               org.clojure/tools.deps.alpha {:mvn/version "0.8.695"}}}

            :test {:extra-paths []}

            :poly {:main-opts ["-m" "polylith.clj.core.poly-cli.core"]
                   :extra-deps {polyfy/polylith
                                {:git/url   "https://github.com/polyfy/polylith"
                                 :sha       "78b2c77c56d1b41109d68b451069affac935200e"
                                 :deps/root "projects/poly"}}}}}
```

If you wonder what all the settings are for, be patient, everything will soon be covered in detail.

## Development

When working with a Polylith codebase, we are free to choose any editor/IDE we like, for example
[Emacs](https://www.gnu.org/software/emacs/)/[Cider](https://github.com/clojure-emacs/cider), 
[VSCode](https://code.visualstudio.com/)/[Calva](https://marketplace.visualstudio.com/items?itemName=betterthantomorrow.calva) or
[IDEA](https://www.jetbrains.com/idea/)/[Cursive](https://cursive-ide.com). 
Here we will use Cursive, and if you do, make sure you have [tools.deps](https://cursive-ide.com/userguide/deps.html) configured correctly.

Let's get started by creating a project. From the menu, select `File > New > Project from existing sources`.
Select the `deps.edn` file, the desired version of SDK and finish the wizard.

Make sure to activate the `:dev` alias (and press the "two arrows" icon to refresh):<br>
<img src="images/dev-alias.png" width="30%">

Let's create a REPL by clicking `Add Configuration`:<br>
<img src="images/add-configuration.png" width="20%">

Click the `+` sign and select `Clojure REPL > Local`:<br>
<img src="images/new-local-repl.png" width="30%">

Fill in:
- Name: REPL
- Which type of REPL to run: nREPL
- Run with Deps: (select)
- Aliases: test,dev

Press OK and start the REPL in debug mode, by clicking the bug icon:<br>
<img src="images/repl-start.png" width="20%">

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

This gives us access to the `development/src` directory so that we can work 
with the code. Right now there is only one directory here, but every time we create a new component or base,
we normally add them to the path too (the exception is if you have several components sharing the
same interface, but more on that later).

The "development/src" path belongs to the `dev` alias which we activated previously and also added to the REPL
by selecting the "dev,test" aliases.
This means that we have configured everything that [tools.deps](https://github.com/clojure/tools.deps.alpha)
needs and that we are ready to write some Clojure code!

To do that we first need to create a namespace. We suggest that you use `dev` as a top namespace here and not 
the workspace top namespace `se.example`.
The reason is that we don't want to mix the code we put here with production code.

One way of structuring the code is to give all developers their own namespace under the `dev` top namespace.
Let's follow that pattern and create the namespace `dev.lisa`.

Right click on the `development/src` directory and select `New > Clojure Namespace` and type "dev.lisa":<br>
<img src="images/new-namespace.png" width="30%">

When this dialog turns up, select "Remember, don't ask again" and click the `Add` button.

<img src="images/add-files-to-git-dialog.png" width="40%">

Now let's write some code:
```clojure
(ns dev.lisa)

(+ 1 2 3)
```
Make sure the namespace is loaded, by sending `(ns dev.lisa)` to the REPL.
If we then send `(+ 1 2 3)` to the REPL we should get `6` back, and if we do,
it means that we now have a working development environment!

# Component

<img src="images/component.png">

Now when we have a working development environment, let's continue and create our first component,
by executing the [create component](doc/commands.md#create-component) command:
```sh
cd example
poly create component name:user
```


Our workspace will now look like this:
```sh
example
├── bases
├── components
│   └── user
│       ├── resources
│       │   └── user
│       │       └── .keep
│       ├── src
│       │   └── se
│       │       └── example
│       │           └── user
│       │               └── interface.clj
│       └── test
│           └── se
│               └── example
│                   └── user
│                       └── interface_test.clj
├── deps.edn
├── development
│   └── src
│       └── dev
│           └── lisa.clj
├── logo.png
├── projects
├── readme.md
└── workspace.edn
```

The command also printed out this message:
```
  Remember to add src, resources and test directories to 'deps.edn' files.
```

This was a reminder for us to add source directories to `deps.edn`.
If we don't, then tools.deps and the development environment will not recognise our newly created component,
which would be a pity!
The tool leaves this task to you as a developer, with the idea to give you as much control as possible
(files are only edited by you, not by the tool).

Let's continue by adding the component's `src`, `resources` and `test` directory to `deps.edn`:
```clojure
 :aliases  {:dev {:extra-paths ["development/src"
                                "components/user/src"
                                "components/user/resources"]
  ...
            :test {:extra-paths ["components/user/test"]}
```

Now we may need to refresh our IDE, by clicking this link, or the icon we used before:<br>
<img src="images/refresh-ws.png" width="40%">

Now execute the [info](doc/commands.md#info) command:<br>
```sh
poly info
```
<img src="images/component-info.png" width="30%">

This tells us that we have one `development` project, one `user` component and 
one `user` interface but no base (yet). Components and bases are referred to as `bricks`
(we will soon explain what a base is). 
The cryptic `x--` and `xx-` will be described in the [flags](#flags) section.

If your colors don't look as nice as this, then visit the [colors](#colors) section.

Now, let's add the `core` namespace to `user`:<br>
<img src="images/ide-ws.png" width="30%">

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
which is the recommended way of structuring the code in Polylith.
Here we put all our implementing code in one single namespace, but as the codebase grows, 
more namespaces can be added to the component when needed.

## Interface

<img src="images/component-interface.png">

Component interfaces give a number of benefits:
- _Single point of access_. Components can only be accessed through their interface, which makes them
   easy to find, use and reason about.
- _Encapsulation_. All the implementing namespaces for a component can be changed without breaking the interface contract.
- _Composability_. All components have access to all other components via interfaces, and can be replaced as long as they use the same interface.

When we created the `user` component, the `user` interface was also created.

So what is an `interface` and what is it good for?

An interface in the Polylith world is a namespace named `interface` that often lives in one but 
sometimes several namespaces within a component. It defines a number of `def`, `defn` or `defmacro`
statements which forms the contract that it exposes to other components and bases.

If more than one component uses the same interface, then all these components must define the exact same set of 
`def`, `defn` and `defmacro` definitions, which is something the tool helps us with.

To give an example, let's pretend we have the interface `user` containing the functions
`fun1` and `fun2` and that two components "implement" this interface, e.g:
```
▾ myworkspace
  ...
  ▾ components
    ▾ user
      ▾ src
        ▾ com
          ▾ another-example
            ▾ user
                interface.clj
                  fun1
                  fun2
                ...
    ▾ admin
      ▾ src
        ▾ com
          ▾ another-example
            ▾ user
                interface.clj
                  fun1
                  fun2
                ...
  ...
```

Now we are free to edit the `interface.clj` file for both `user` and `admin`, which means they can 
get out of sync if we are not careful enough. Luckily, the Polylith tool will help us
keep them consistent, and complain if they differ when we run the [check](#check), 
[info](#info) or [test](#test) commands!

We often choose to have just a single `interface` namespace in a component, but it's also possible to 
divide the interface into several namespaces.
To do so we first create an `interface` package (directory) with the name `interface` at the root
and then we put the sub namespaces in there.

We can find an example where the Polylith tool does that, by dividing its 
[util](https://github.com/polyfy/polylith/tree/master/components/util/src/polylith/clj/core/util/interface)
interface into several sub namespaces:
```sh
util
└── interface
    ├── color.clj
    ├── exception.clj
    ├── os.clj
    ├── str.clj
    └── time.clj
```

This can be handy if we want to group the functions and not put everyone into one place.
A common usage is to place [clojure specs](https://clojure.org/about/spec) in its own `spec` sub namespace.

Every time you think of splitting up the interface, keep in mind that it may be an indicator
that it's instead time to split up the component into smaller components!

Here is an example of some code that uses such an interface:
```clojure
(ns dev.lisa
  (:require [se.example.util.interface.time :as time-util]))

(time-util/current-time)
```
### Interface definitions

So far, we have only used functions in the interface. Polylith also supports having `def`
and `defmacro` statements in the interface.
There is no magic here, just include the definitions you want, like this:
```clojure
(def one-two-three 123)
```
Now it can be used as a normal definition from any other component or base.

A `defmacro` definition can look like this:
```clojure
(ns se.example.logger.interface
  (:require [se.example.logger.core :as core]))

(defmacro info [& args]
  `(core/info ~args))
```

...which delegates to:
```clojure
(ns se.example.logger.core
  (:require [taoensso.timbre :as timbre]))

(defmacro info [args]
  `(timbre/log! :info :p ~args))
``` 

This list of tips makes more sense when you have used Polylith for a while, 
so take note of this section for later:
- Functions can be sorted in alphabetical order in the interface, while we can freely arrange them in the implementation namespace(s).
- The interface can expose the name of the entity, e.g. `sell [car]`, while the implementing function can do the
  destructuring, e.g. `sell [{:keys [model type color]}]` which sometimes can improve the readability.
- If we have a [multi-arity function](http://clojure-doc.org/articles/language/functions.html#multi-arity-functions)
  in the interface, a simplification can sometimes be to have a single arity function in the implementing
  namespace that allows some parameters to be passed in as `nil`.
- If using [variadic functions](http://clojure-doc.org/articles/language/functions.html#variadic-functions)
  in the interface, a simplification is to pass in what comes after `&` as a `list` to the implementing function.
- Testing is simplified by allowing access to implementing namespaces from the `test` directory.
  Only the code under the `src` directory is restricted to only access the `interface` namespace.
  The check is performed when running the `check`, `info` or `test`command.
- All functions can be declared public while still being protected. This improves testability and the debugging experience.
  When stopping at a breakpoint to evaluate a function, we don't need to use any special syntax to access it, 
  that we otherwise would have to if it was private.
- If using a `function` in two components that implement the same interface,
  all definitions must be `function`. The same goes for `macros`. The reason for this restriction is that 
  functions are composable, but macros are not, which could otherwise cause problems.

Finally, if we have really good reasons to, the `interface` namespace name can be changed in `:interface-ns` in `./deps.edn`.

## Base

<img src="images/base.png">

A `base` is similar to a `component` except for two things:
- It doesn't have an `interface`.
- It exposes a public API to the outside world.

The lack of an `interface` makes bases less composable compared to components.
This is okay, because they serve a different purpose which is to be a bridge between 
the real world and the components the base delegates to.
This gives us the modularity and structure we need to build simple and understandable 
services, tools, and libraries.

Let's create the `cli` base to see how it works, by executing the [create base](doc/commands.md#create-base) command:
```sh
poly create base name:cli
```

Our workspace should now look like this:
```sh
example
├── bases
│   └── cli
│       ├── resources
│       │   └── cli
│       ├── src
│       │   └── se
│       │       └── example
│       │           └── cli
│       │               └── core.clj
│       └── test
│           └── se
│               └── example
│                   └── cli
│                       └── core_test.clj
├── components
│   └── user
│       ├── resources
│       │   └── user
│       ├── src
│       │   └── se
│       │       └── example
│       │           └── user
│       │               ├── core.clj
│       │               └── interface.clj
│       └── test
│           └── se
│               └── example
│                   └── user
│                       └── interface_test.clj
├── deps.edn
├── development
│   └── src
│       └── dev
│           └── lisa.clj
├── logo.png
├── projects
├── readme.md
└── workspace.edn
```

Now we need to update `deps.edn` with our newly created base:
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

...and add some code to it:
```clojure
(ns se.example.cli.core
  (:require [se.example.user.interface :as user])
  (:gen-class))

(defn -main [& args]
  (println (user/hello (first args)))
  (System/exit 0))
```

Here we added the `-main` function that will later be called from the command line.
The `(:gen-class)` statement tells the compiler to generate a Java class for us
when the code is compiled.

The next thing we want to do is to build an artifact that will turn the code into something useful, a command line tool.
To do that, we need to start by creating a project.

## Project
<img src="images/project.png">

There are two kinds of projects in Polylith: development and deployable.

1. The `development` project:
   - This is where we work with the code, often from a REPL. 
   - It contains all libraries, components and bases in the workspace, which is specified in `./deps.edn`.
   - If we have any [profiles](#profile) then they are defined in `./deps.edn`.
   - Any extra code, that is not part of a component or base, lives under the `development` folder.
2. Any `deployable` project:
   - Used to build deployable artifacts, e.g.: lambda functions, REST API's, libraries, tools, ...and more.
   - Lives under the `projects` directory where each project has its own directory.
   - Has a `deps.edn` config file that specifies which libraries, component and bases that are included.
   - Can optionally have a `resources` directory. 
   - If the base (we normally have only one per project) and the components that belong to it,
     contain any tests, then they will be run when we execute the [test](#test) command.
   - If it has any tests of its own, they will live in the `test` directory, e.g. `projects/my-project/test`. 
   - It's discouraged to have a `src` directory since all production code should normally only live in components and bases.

Let's create a project, by executing the [create project](doc/commands.md#create-project) command:
```sh
poly create project name:command-line
```
 
Our workspace should now look like this:
```sh
example
├── bases
│   └── cli
│       ├── resources
│       │   └── cli
│       ├── src
│       │   └── se
│       │       └── example
│       │           └── cli
│       │               └── core.clj
│       └── test
│           └── se
│               └── example
│                   └── cli
│                       └── core_test.clj
├── components
│   └── user
│       ├── resources
│       │   └── user
│       ├── src
│       │   └── se
│       │       └── example
│       │           └── user
│       │               ├── core.clj
│       │               └── interface.clj
│       └── test
│           └── se
│               └── example
│                   └── user
│                       └── interface_test.clj
├── deps.edn
├── development
│   └── src
│       └── dev
│           └── lisa.clj
├── logo.png
├── projects
│   └── command-line
│       └── deps.edn
├── readme.md
└── workspace.edn
```
 
The tool also reminds us of this:
```sh
  It's recommended to add an alias to :projects in ./workspace.edn for the command-line project.
```

If we don't add the alias to `./deps.edn`, the project heading will show up as `?` when we execute the `info` command,
so let's add it:
```clojure
{:polylith {...
            :projects {"development" {:alias "dev", :test []}
                                "command-line" {:alias "cl"}
```

Now add `user` and `cli` to `projects/command-line/deps.edn`:
```clojure
{:paths ["../../components/user/src"
         "../../components/user/resources"
         "../../bases/cli/src"
         "../../bases/cli/resources"]
 ...

 :aliases {:test {:extra-paths ["../../components/user/test"
                                "../../bases/cli/test"]
                  ... }}}
```

Note:
- We didn't add the path "development/src".
- The src paths and the test paths are configured at different levels, `:paths` and `extra-paths`.
- All paths begin with "../../".

The reason we didn't add "development/src" is because it contains code that should only be used
from the development environment.

All projects under the `projects` directory have their source paths defined in `:paths`
instead of inside the `:dev` alias, as for the `development` project.
The deployable projects are simpler than `development` and use the "standard way" of 
configuring projects by putting things in `:paths`.

The reason all paths begin with "../../" is that `components` and `bases` live two levels up 
compared to `projects/command-line` and not at the root as with the `development` project.

If we add a missing path here, then we will get a warning when we execute the [check](#check) or [info](#info) command, e.g.:
<img src="images/warning.png" width="90%">

Let's summarise where the paths are located:
- The dev project: `./deps.edn` > `:aliases` > `:dev` > `:extra-paths`.
- Other projects: `projects/PROJECT-DIR` > `deps.edn` > `:paths`.

## Tools.deps

This Polylith tool is built on top of _tools.deps_. To get the most out of it, we recommend 
you to read its [documentation](https://github.com/clojure/tools.deps.alpha).

To make it easier to follow the examples in the next `build` section, we will show some examples
on how to use the `clj` command (the `clojure` command will also work in these examples).

If you are already comfortable with tools.deps, then you can skip directly to the [build](#build) section.
For the rest of you, we'll go through the step-by-step process of compiling our new project to an uberjar.

Let's start by compiling the `command-line` project:
```
cd projects/command-line
mkdir -p classes
clj -e "(compile,'se.example.cli.core)"
```
This will AOT compile the `command-line` project.
The command needs the `classes` directory, so we have to create it first.

If we add this `alias` to `command-line/deps.edn` (which we will do in the next section):
```clojure
 :aliases {:aot   {:extra-paths ["classes"]
                   :main-opts   ["-e" "(compile,'se.example.cli.core)"]}
           ...
```

...we can compile the project by giving the `aot` alias:
```sh
clj -A:aot
```

To build an uberjar, out of the compiled classes, we need to add this alias:
```clojure
           :uberjar {:extra-deps {uberdeps {:mvn/version "0.1.10"}}
                     :main-opts  ["-m" "uberdeps.uberjar"
                                  "--aliases" "aot"
                                  "--main-class" "se.example.cli.core"]}
           ...
```

...and execute:
```
clj -A:uberjar
```

When we created the workspace with the [create workspace](doc/commands.md#create-workspace) command, the `poly` alias was also added to `./deps.edn`:
```clojure
            :poly {:main-opts ["-m" "polylith.clj.core.poly-cli.core"]
                   :extra-deps {polyfy/polylith
                                {:git/url   "https://github.com/polyfy/polylith.git"
                                 :sha       "INSERT_LATEST_SHA_HERE"
                                 :deps/root "projects/poly"}}}
```

Make sure you have updated the `sha` from `INSERT_LATEST_SHA_HERE` to the sha of the [latest commit](https://github.com/polyfy/polylith/commits/master). 

This alias can now be used to execute the `poly` tool from the workspace root, e.g.:
```
clj -A:poly info
```

It takes longer to execute the `poly` command this way, because it needs to compile the Clojure code 
first, but it also allows us to execute older or newer versions of the tool by
selecting another `sha` from an [existing commit](https://github.com/polyfy/polylith/commits).

## Build

The Polylith tool doesn’t include a `build` command.
That’s because we don’t want the tool to restrict our build pipeline in any way. 
Instead, the tool lets us choose our own way to build our Polylith artifacts for our particular pipeline; 
which could be with simple build scripts, all the way to cloud-based build tools.

Let's say we want to create an executable jar file out of the `command-line` project.  
First, we create a `scripts` directory at the workspace root and copy this [build-uberjar.sh](https://github.com/polyfy/polylith/blob/master/scripts/build-uberjar.sh)
to it:
```sh
example
├── scripts
│   └── build-uberjar.sh
```

Create `build-cli-uberjar.sh`:
```sh
example
├── scripts
│   ├── build-uberjar.sh
│   └── build-cli-uberjar.sh
```

...with this content:
```sh
#!/usr/bin/env bash
./build-uberjar.sh command-line
```

...and make sure both are executable:
```sh
chmod +x scripts/build-uberjar.sh
chmod +x scripts/build-cli-uberjar.sh
```

Now add the `aot` and `uberjar` aliases to `deps.edn` in `projects/command-line`
(if you followed the instructions in the tools.deps section, you have already done this):
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
                     :main-opts  ["-m" "uberdeps.uberjar"
                                  "--aliases" "aot"
                                  "--main-class" "se.example.cli.core"]}}}

```

The `aot` alias points to the `se.example.cli.core` namespace, which is where our `-main` function lives.
The `uberjar` alias is used to create a callable uberjar (you can read more about uberjars [here](https://github.com/tonsky/uberdeps)).

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
cd ../projects/command-line/target
java -jar command-line.jar Lisa
```

```
Hello Lisa!
```

Nice, it worked!

## Git

We have already used the [info](#info) command a couple of times without explaining everything in its output.

Let's execute the `info` command again to see the current state of the workspace:<br>
<img src="images/git-info.png" width="30%">

At the top we have the line `stable since: c91fdad`. 
To explain what this is, let's take it from the beginning.

When a Polylith workspace is created, these `git` commands are executed:
```
git init
git add .
git commit -m "Workspace created."
``` 

If we run `git log` from the workspace root, it returns something like this:
```sh
commit c91fdad4a34927d9aacfe4b04ea2f304f3303282 (HEAD -> master)
Author: lisa <lisa@gmail.com>
Date:   Thu Sep 3 06:11:23 2020 +0200

    Workspace created.
```

This is the first and only commit of this repository so far.
This is also the first `stable point in time` of this workspace which the tool uses when it calculates what changes have
been made (up till now). Notice that the first letters of the hash correspond to `stable since: c91fdad`
and this is because it refers to this SHA-1 hash in git.
 
The `command-line` and `development` projects, and the `user` and `cli` bricks 
are all marked with an asterisk, `*`. The way the tool calculates changes is to ask
`git` by running this command internally:
```sh
git diff c91fdad4a34927d9aacfe4b04ea2f304f3303282 --name-only
```

We can also run the [diff](#diff) command, which will execute the same git statement internally:
```clojure
poly diff
```

The output is the same (this assumes that you have [added](https://git-scm.com/docs/git-add) the files to your git repository):
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
projects/command-line/deps.edn
scripts/build-cli-uberjar.sh
scripts/build-uberjar.sh
```

Here we have the answer to were the `*` signs come from. The paths that start with `projects/command-line/`, 
`development/`, `components/user/` and `bases/cli/` makes the tool understand that `command-line`, `development`,
`user` and `cli` are changed.

When we created the workspace, a [.gitignore](https://git-scm.com/docs/gitignore) file was also created for us.  
Now is a good time to add more rows here if needed:
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

```sh
e7ebe683a775ec28b7c2b5d77e01e79d48149d13 (HEAD -> master) Created the user and cli bricks.
c91fdad4a34927d9aacfe4b04ea2f304f3303282 Workspace created.
```

If we run the `info` command again, it will return the same result as before, and the reason is that we
haven't told git to move the `stable point in time` to our second commit.

# Tagging

Tags are used in Polylith to mark points in time where we concider the whole codebase (workspace)
to be in a valid state, for example that everything compiles and that all the tests and the `check` command executes 
without errors. 
This is then used by the [test](#test) command to run the tests incrementally, by only executing
the affected tests, which substantially speeds up the tests.

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
Note that your hash tags will be different and when we refer to e.g. `c91fdad`
in the following examples, you should instead give your own corresponding hash code.
 
If we execute the `info` command:

<img src="images/tagging-info-1.png" width="30%">

...the `stable since` hash has been updated and is now tagged with `stable-lisa`.
All the `*` signs are gone because no `component`, `base` or `project` 
has yet changed since the second commit (which can be verified by running `poly diff` again).

We added the tag `stable-lisa` but we could have named the tag with anything that starts with `stable-`.
We choose `stable-lisa` because Lisa is our name (let's pretend that at least!). The idea is that every developer could use
their own unique tag name that doesn't conflict with other developers. 

The CI build should also use its own pattern, like `stable-` plus branch name or build number, to mark successful builds.
It may be enough to only use the stable points that the CI server creates. That is at least a good way to start out
and only add custom tags per developer when needed.

The pattern is configured in `deps.edn` and can be changed if we prefer something else:
```clojure
            :stable-tag-pattern "stable-*"
```

It's possible to move back to an earlier `stable point` in time by passing in a hash (the first few letters is enough
as long as it's unique) - but let's not do that now:
```sh
git tag -f stable-lisa c91fdad
```

The way the tool finds the latest tag is to execute this command internally:
```
git log --pretty=format:'%H %d'
```

Then it uses the first line of the output that matches the `stable-*` regular expression,
or if no match was found, the first commit in the repository.

### Release

When we release, we probably want the CI server to tag the release. Here we tag the first commit as `v1.1.0`
and the second as `v1.2.0` (make sure you replace `c91fdad` with your corresponding sha):
```
git tag v1.1.0 c91fdad
git tag v1.2.0
```

If we execute:
```
poly info since:release
```
<img src="images/tagging-info-2.png" width="27%">

...it picks the latest release tag that follows the pattern defined in `./deps.edn`:
```
            :release-tag-pattern "v[0-9]*"
```

If we execute:
```
poly info since:previous-release
```
<img src="images/tagging-info-3.png" width="27%">

...it picks the second latest release tag.

By executing `git log --pretty=oneline` we can verify that the tags are correctly set:

```
e7ebe683a775ec28b7c2b5d77e01e79d48149d13 (HEAD -> master, tag: v1.2.0, tag: stable-lisa) Created the user and cli bricks.
c91fdad4a34927d9aacfe4b04ea2f304f3303282 (tag: v1.1.0) Workspace created.
```

The `since` parameter is used by the CI server to run all tests since the previous release, e.g.:
```
poly test since:previous-release
```

Depending on whether we tag before or after the build, we will choose `release` or `previous-release`.
If `since` is not given, `last-stable` will be used by default.

## Flags

We have one more thing to cover regarding the `info` command, and that is what the `x` and `-` flags mean:
<img src="images/project-flags.png" width="25%">

Each flag under _source_ has a different meaning:<br>
<img src="images/flags.png" width="30%">

The `---` for the `command-line` project means we have a `projects/command-line`
directory but no `src` or `test` directories in it and that no tests will be executed for this project.

The `x--` for the `development` project means we have a `development/src` directory
but no `development/test` directory and that no tests will be executed for this project.

We also have this section:<br>
<img src="images/brick-flags.png" width="25%">

Here the flags have a slightly different meaning:<br>
<img src="images/flags-included.png" width="38%">

The `xx-` for the `user` component tells that both `components/user/src` and `components/user/test` 
are included in the `command-line` and `development` projects and that no brick tests will be executed.

The `xx-` for the `cli` base follows the same pattern as for the `user` component but for the
`bases/cli` directory.

The bricks for the `command-line` project is configured in `projects/command-line/deps.edn`:
```clojure
{:paths ["../../components/user/src"
         "../../bases/cli/src"
         "../../bases/cli/resources"]

 ...
 :aliases {:test {:extra-paths ["../../components/user/test"
```

The bricks for the `development` project is configured in `./deps.edn`:
```clojure
 :aliases  {:dev {:extra-paths [...
                                "components/user/src"
                                "components/user/resources"
  ...
            :test {:extra-paths ["components/user/test"
```

If we execute `poly info :r` (or the longer `poly info :resources`):<br>
<img src="images/flags-info.png" width="30%">


...then the `resources` directory is also shown:<br>
<img src="images/flags-resources.png" width="20%">

## Testing

Polylith encourages a test-centric approach when working with code. New brick tests are easy to 
write, and mocking can be avoided in most cases as we have access to all components from the 
projects they live in.

Let's go back to our example.

Nothing is marked to be tested at the moment, but if we change the `core` namespace in the `user` component
by adding an extra `!`, that should do the trick:
```clojure
(ns se.example.user.core)

(defn hello [name]
  (str "Hello " name "!!"))
```

We can verify that the tool recognises the change by running the `diff` command, which will give us this output:
```
components/user/src/se/example/user/core.clj
```

...and if we run the `info` command again:<br>
<img src="images/testing-info-1.png" width="30%">

...the `user` component is now marked with an asterisk, `*`. If we look carefully we will also notice that 
the status flags `xxx` under the `cl` column now has an `x` in its last position. As we already know, 
this means that the tests for `user` and `cli` will be executed from the `command-line` project
if we execute the `test` command.
  
But why is `cli` marked to be tested? The reason is that even though `cli` itself hasn't changed, 
it depends on something that has, namely the `user` component.  
  
The columns under the `development` project are all marked as `xx-`. The reason the `development`
project is not marked to be tested is that the `development` project's tests are 
not included by default.

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

Now we can run the test from the IDE:
- Make sure the namespace is loaded, e.g. via the menu (or keyboard shortcuts) `Tools > REPL > Load File in REPL`
- Run the test, e.g:
  - Run all tests in the current namespace: `Tools > REPL > Run Tests in Current NS in REPL`
  - Or, place the cursor under the test and run: `Tools > REPL > Run Test under carret in REPL`

Oops, the test failed!

<img src="images/test-in-repl-failing.png" width="50%">

And if we run the [test](#test) command:
```sh
poly test
```

...it fails here too:

```
projects to run tests from: command-line

Running tests from the command-line project, including 2 bricks: user, cli

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

Remember that we added an extra `!` so now we need to update the 
corresponding test accordingly:
```clojure
(ns se.example.user.interface-test
  (:require [clojure.test :refer :all]
            [se.example.user.interface :as user]))

(deftest hello--when-called-with-a-name--then-return-hello-phrase
  (is (= "Hello Lisa!!"
         (user/hello "Lisa"))))
```

If we run the test again from the REPL, it will now turn to green:

<img src="images/test-in-repl-success.png" width="50%">

...and the `test` command will pass too:
```
projects to run tests from: command-line

Running tests from the command-line project, including 2 bricks: user, cli

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


We have already mentioned that the brick tests will not be executed from the `development` project
when we run the `test` command.
But there is a way to do that, and that is to pass in `:dev` or `project:dev`.  

Let's try it out with the `info` command first:
```sh
poly info :dev
```
<img src="images/testing-info-2.png" width="30%">

And yes, now the tests for the `development` project are included. When we give a project 
using `project` (`:dev` is a shortcut for `project:dev`) only that project will be included. 
One way to test both the `development` project and the `command-line` project is to 
select both:
```
poly info project:cl:dev
```
<img src="images/testing-info-3.png" width="30%">

Now both the `development` and the `command-line` project is marked for test execution.
Here we used the project aliases `cl` and `dev` but we could also have passed in the project 
names or a mix of the two, e.g. `poly info project:command-line:dev`.  

### Project tests

Before we execute any tests, let's add a project test for the `command-line` project.

Begin by adding a `test` directory for the `command-line` project:
```sh
example
├── projects
│   └── command-line
│       └── test
``` 

Then add the "test" path to `projects/command-line/deps.edn`:
```clojure
            :test {:extra-paths ["../../components/user/test"
                                 "../../bases/cli/test"
                                 "test"]}
```

...and to `./deps.edn`:
```clojure
            :test {:extra-paths ["components/user/test"
                                 "bases/cli/test"
                                 "projects/command-line/test"]}
```

Now add the `project.dummy-test` namespace to the `command-line` project:
```sh
example
├── projects
│   └── command-line
│       └── test
│           └── project
│               └──dummy-test.clj

```
```clojure
(ns project.dummy-test
  (:require [clojure.test :refer :all]))

(deftest dummy-test
  (is (= 1 1)))
```

We could have chosen another top namespace, e.g., `se.example.project.command-line`, as long as 
we don't have any brick with the name `project`. But because we don't want to get into any name
conflicts with bricks and also because each project is executed in isolation, the choice of 
namespace is less important and here we choose the `project` top namespace to keep it simple. 

Normally, we are forced to put our tests in the same namespace as the code we want to test,
to get proper access, but in Polylith the encapsulation is guaranteed by the Polylith Tool and
all code can therefore be declared public, which allows us to put the test code wherever we want.

If we execute the `info` command:<br>
<img src="images/testing-info-4.png" width="30%">

...the `command-line` is marked as changed and flagged as `-x-` telling us that 
it now has a `test` directory.  
The reason it is not tagged as `-xx` is that project tests 
are not marked to be executed without explicitly telling them to, by passing in `:project`.

```sh
poly info :project
```
<img src="images/testing-info-5.png" width="30%">

Now the `command-line` project is also marked to be tested.
Let's verify that by running the tests:
```sh
poly test :project
```

```
projects to run tests from: command-line

Running tests from the command-line project, including 2 bricks and 1 project: user-remote, cli, command-line

Testing se.example.cli.core-test

Ran 0 tests containing 0 assertions.
0 failures, 0 errors.

Test results: 0 passes, 0 failures, 0 errors.

Testing se.example.user.interface-test

Ran 1 tests containing 1 assertions.
0 failures, 0 errors.

Test results: 1 passes, 0 failures, 0 errors.

Testing project.dummy-test

Ran 1 tests containing 1 assertions.
0 failures, 0 errors.

Test results: 1 passes, 0 failures, 0 errors.
Execution time: 2 seconds
```

They passed!

### Test approaches

As you have just seen, with Polylith we can add tests at two different levels: brick and project.

The _project_ tests should be used for our slow tests, e.g. tests that takes more than 100 miliseconds
to execute, or whatever we draw the line, to keep our fast _brick_ tests fast enough to give us
a really fast feedback loop.
The project tests also give us a way to write tailor-made tests that are unique per project.

The second category is the _brick_ tests.
To keep the feedback loop short, we should only put fast running tests in our bricks.
This will give us a faster feedback loop, because the brick tests are the ones
that are executed when we run `poly test` while the project tests are not.

But does that mean we are only allowed to put unit tests in our bricks?  
No. As long as the tests are fast (by e.g. using in-memory databases)
they should be put in the bricks they belong to.

Before we continue, let's commit what we have done so far and mark the workspace as stable:
```sh
git add --all
git commit -m "Added tests"
git tag -f stable-lisa
```
If we execute the `info` command again:<br>
<img src="images/testing-info-6.png" width="30%">

...the `*` signs are now gone and nothing is marked to be tested.

The tool only executes tests if a brick is directly or indirectly changed.  
A way to force it to test all bricks is to pass in `:all-bricks`:
```sh
poly info :all-bricks
```
<img src="images/testing-info-7.png" width="30%">

Now all the brick tests are marked to be executed, except for the `development` project.  
To include dev, also add `:dev`:
```sh
poly info :all-bricks :dev
```
<img src="images/testing-info-8.png" width="30%">

To include all brick and project tests (except `dev`) we can type:
```sh
poly info :all
```
<img src="images/testing-info-9.png" width="30%">

...to also include dev, type:
```
poly info :all :dev
```
<img src="images/testing-info-10.png" width="30%">

Running the brick tests from the `development` projects is something we don't normally need to do,
but it's good to know that it's supported.

Now let's see if it actually works:
```sh
poly test :all :dev
```
```
Projects to run tests from: command-line, development

Running tests from the command-line project, including 2 bricks and 1 project: user, cli, command-line

Testing se.example.cli.core-test

Ran 0 tests containing 0 assertions.
0 failures, 0 errors.

Test results: 0 passes, 0 failures, 0 errors.

Testing se.example.user.interface-test

Ran 1 tests containing 1 assertions.
0 failures, 0 errors.

Test results: 1 passes, 0 failures, 0 errors.

Testing project.dummy-test

Ran 1 tests containing 1 assertions.
0 failures, 0 errors.

Test results: 1 passes, 0 failures, 0 errors.
Running tests from the development project, including 2 bricks and 1 project: user, cli, command-line

Testing se.example.cli.core-test

Ran 0 tests containing 0 assertions.
0 failures, 0 errors.

Test results: 0 passes, 0 failures, 0 errors.

Testing se.example.user.interface-test

Ran 1 tests containing 1 assertions.
0 failures, 0 errors.

Test results: 1 passes, 0 failures, 0 errors.
Execution time: 3 seconds
```

Looks like it worked!

Let's summarize the different ways to run the tests. 
The brick tests are executed from all projects they belong to except for the development project
(if not `:dev` is passed in):

| Command                    | Tests to execute                                                                             |
|:---------------------------|:---------------------------------------------------------------------------------------------|
| poly test                  | All brick tests that are directly or indirectly changed. |
| poly test :project         | All brick tests that are directly or indirectly changed + tests for changed projects. |
| poly&nbsp;test&nbsp;:all&#8209;bricks | All brick tests. |
| poly test :all             | All brick tests + all project tests (except development). |

To also execute the brick tests from the development project, pass in `:dev`:

| Command                    | Tests to execute                                                                             |
|:---------------------------|:---------------------------------------------------------------------------------------------|
| poly test :dev              | All brick tests that are directly or indirectly changed, only executed from the development project. |
| poly test :project :dev     | All brick tests that are directly or indirectly changed, executed from all projects (development included) + tests for changed projects (development included). |
| poly&nbsp;test&nbsp;:all&#8209;bricks&nbsp;:dev | All brick tests, executed from all projects (development included). |
| poly test :all :dev         | All brick tests, executed from all projects (development included) + all project tests (development included). |

Projects can also be explicitly selected with e.g. `project:proj1` or `project:proj1:proj2`. `:dev` is a shortcut for `project:dev`. 

These arguments can also be passed in to the `info` command, as we have done in the examples above, 
to get a view of which tests will be executed.

## Profile

When working with a Polylith system, we want to keep everything as simple as possible
and maximize our productivity.
The Lego-like way of organising code into bricks, helps us with both of these goals.

One problem we normally have when developing software without using Polylith, is that the production environment
and the development environment has a 1:1 relationship. This happens because we use the production codebase
for development, so if we create a new service in production, it will automatically
"turn up" in the development project.
 
In Polylith we avoid this problem by separating the development project from production.
Thanks to components, we can create any project we want by putting the bricks we need into one place.
This allows us to optimize the development environment for productivity while in production, we can
focus on fulfilling non functional requirements like performance or up time. 

Right now, our `development` project mirrors the `command-line` project:<br>
<img src="images/command-line.png" width="35%">

Let's pretend we get performance problems in the `user` component and that we think
distributing the load, by delegating to a new service, could solve the problem:<br>
<img src="images/dev-and-prod.png" width="57%">

The production environment now looks good, but how about the `development` environment?
The problem here is that it contains two components that share the same `user` interface.
This will confuse both the classloader (if we start a REPL) and the IDE, because we now have
two components using the same `se.example.user` namespace in the path, which is not a desirable situation.

The solution is to use `profiles`:<br>
<img src="images/development.png" width="62%">

By leaving out any component that implements the `user` interface from the `development` 
project and combining it with one of the two possible `profiles` we get a complete development
project. This allows us to work with the code from a single place, but still be 
able to mimic the various projects we have.

The `default` profile (if exists) is automatically merged into the `development` project, if no other profiles
are selected. The name `default` is set by `:default-profile-name` in `./deps.edn` and can be changed,
but here we will leave it as it is.

Now let's try to move from this design:<br>
<img src="images/command-line.png" width="35%">

...to this:<br>
<img src="images/target-architecture.png" width="58%">

First we need to decide how the `command-line` tool should communicate with `user-service` over the wire.
After some searching, we found this [slacker](https://github.com/sunng87/slacker) library that 
allows us to use [remote procedure calls](https://en.wikipedia.org/wiki/Remote_procedure_call) 
in a simple way.

Let's create a checklist that will take us there:
1. Create the `user-api` base.
2. Create the `user-remote` component.
3. Switch from `user` to `user-remote` in `deps.edn` for the `command-line` project.
4. Create the `user-service`.
5. Create a build script for `user-service`.

Let's go through the list.

#### 1. Create the `user-api` base:

- [x] Create the base.
- [x] Add paths to `./deps.edn`.
- [x] Add `slacker` related libraries to `./deps.edn`.
- [x] Add library mapping for the `slacker` library to `./deps.edn`.
- [x] Implement the server for `user-api`:

Execute this statement:
```
poly create base name:user-api
```

Add `user-api` paths to `./deps.edn`:
```
 :aliases  {:dev {:extra-paths [...
                                "bases/user-api/src"
                                "bases/user-api/resources"]

            :test {:extra-paths [...
                                 "bases/user-api/test"
```

Add `slacker` related libraries to `./deps.edn` (libraries are added per project, which is described in the [libraries](#libraries) section):
```
 :aliases  {:dev
            ...

            :test {:extra-paths [...

                  :extra-deps {...
                               slacker {:mvn/version "0.17.0"}
                               http-kit {:mvn/version "2.4.0"}
                               ring {:mvn/version "1.8.1"}
                               compojure {:mvn/version "1.6.2"}
                               org.apache.logging.log4j/log4j-core {:mvn/version "2.13.3"}
                               org.apache.logging.log4j/log4j-slf4j-impl {:mvn/version "2.13.3"}}}
```

Add library mapping for the `slacker` library to `./deps.edn`:
```
{:polylith {...
            :ns-to-lib {slacker  slacker}}
```

This maps the `slacker` namespace to the `slacker` library, so that the tool can figure out
which projects need the `slacker` library, when running the [check](#check) or [info](#info) command.
It also allows the [libs](#libs) command to show which bricks use the `slack` library,
which is explained in detail in the [libraries](#libraries) section.

Create the _api_ namespace:

```
example
├── bases
│   └── user-api
│       └── src
│           ├── se.example.user_api.api.clj
│           └── se.example.user_api.core.clj
```

...with this content:
```clojure
(ns se.example.user-api.api
  (:require [se.example.user.interface :as user]))

(defn hello-remote [name]
  (user/hello (str name " - from the server")))
```
...and update the `core` namespace:

```clojure
(ns se.example.user-api.core
  (:require [se.example.user-api.api]
            [slacker.server :as server])
  (:gen-class))

(defn -main [& args]
  (server/start-slacker-server [(the-ns 'se.example.user-api.api)] 2104)
  (println "server started: http://127.0.0.1:2104"))
```

#### 2. Create the `user-remote` component:

- [x] Create the component.
- [x] Remove the `user` paths from `./deps.edn`.
- [x] Create the `default` and `remote` profiles.
- [x] Activate the `remote` profile in the IDE.
- [x] Activate the `default` profile in the REPL configuration.
- [x] Implement the component:
  - [x] Create the `core` namespace and call `user-service` from there.
  - [x] Delegate from the `interface` to the `core` namespace.

Create the component:
```sh
poly create component name:user-remote interface:user
```

Remove the `user` related paths from `./deps.edn`:
```
:aliases  {:dev {:extra-paths ["...
                               "components/user/src"
                               "components/user/resources"]
 
           ...

           :test {:extra-paths ["components/user/test"
                                ...]}
```

Add the `default` and `remote` profiles to `./deps.edn`:
```
:aliases  {:dev 
           ...

           :test
           ...

           :+default {:extra-paths ["components/user/src"
                                    "components/user/resources"
                                    "components/user/test"]}

           :+remote {:extra-paths ["components/user-remote/src"
                                   "components/user-remote/resources"
                                   "components/user-remote/test"]}
```

Notice here that the profiles contain both `src` and `test` directories.
This works as profiles are only used from the development project.

The next step is to activate the `remote` profile in our IDE:

<img src="images/profile-activate-remote.png" width="18%">

Create the `core` namespace:

```
example
├── components
│   └── user-remote
│       └── src
│           ├── se.example.user_remote.core.clj
│           └── se.example.user_remote.interface.clj
```

...with this content:
```clojure
(ns se.example.user.core
  (:require [slacker.client :as client]))

(declare hello-remote)

(defn hello [name]
  (let [connection (client/slackerc "localhost:2104")
        _ (client/defn-remote connection se.example.user-api.api/hello-remote)]
    (hello-remote name)))
```

...and update the `interface` namespace:
```clojure
(ns se.example.user.interface
  (:require [se.example.user.core :as core]))

(defn hello [name]
  (core/hello name))
```

Edit the REPL configuration:

<img src="images/repl-edit-configuration.png" width="25%">

...and add the `default` profile to `Aiases`: "test,dev,+default"

The reason we have to do this, is because we removed the `user` component from the "main"
paths in `./deps.edn` and now we have to add it via a profile instead.
We need the source code for the `se.example.user.interface` namespace, and we have two
alternatives, the `user` or the `user-remote` component that both use this interface.
The `user` component is a better default because it's simpler and only communicates via
direct function calls without hitting the wire.

For the changes to take affect we now need to restart the REPL. Normally we don't have to do that,
but when adding profiles it's necessary.
 
#### 3. Switch from `user` to `user-remote` in `deps.edn` for the `command-line` project.
- [x] Replace `user` related paths with `user-remote` in `projects/command-line/deps.edn`.
- [x] Add the Slacker library to `deps.edn` for `command-line`.
- [x] Add the log4j library to `deps.edn` for `command-line`.
- [x] Create a `command-line` uberjar.

Update the configuration file for the `command-line` project:

```
example
├── projects
│   └── command-line
│       └── deps.edn
```

Replace `user` with `user-remote`, add the `slacker` library (used by `user-remote`),
and `log4j` (to get rid of warnings):
```clojure
{:paths ["../../components/user-remote/src"
         "../../components/user-remote/resources"
         ...

 :deps {...
        slacker {:mvn/version "0.17.0"}
        org.apache.logging.log4j/log4j-core {:mvn/version "2.13.3"}
        org.apache.logging.log4j/log4j-slf4j-impl {:mvn/version "2.13.3"}}

 :aliases {:test {:extra-paths ["../../components/user-remote/test"
                                ...
```

Create an uberjar by executing:
```
cd scripts
./build-cli-uberjar.sh
cd ..
```

#### 4. Create the `user-service`:

- [x] Create the project.
- [x] Update its `deps.edn`:
  - [x] Add the `slacker` library and libraries it needs.
  - [x] Add paths for the `user` component.
  - [x] Add paths for the `user-api` base.
  - [x] Add the `aot` and `uberjar` aliases.
- [x] Add the `cl` alias for the `user-service` to `./deps.edn`.

Create the project:
```sh
poly create project name:user-service
```

Set the content of `projects/user-service/deps.edn` to this:
```
{:paths ["../../components/user/src"
         "../../components/user/resources"
         "../../bases/user-api/src"
         "../../bases/user-api/resources"]

 :deps {org.clojure/clojure {:mvn/version "1.10.1"}
        org.clojure/tools.deps.alpha {:mvn/version "0.8.695"}
        slacker {:mvn/version "0.17.0"}
        http-kit {:mvn/version "2.4.0"}
        ring {:mvn/version "1.8.1"}
        compojure {:mvn/version "1.6.2"}
        org.apache.logging.log4j/log4j-core {:mvn/version "2.13.3"}
        org.apache.logging.log4j/log4j-slf4j-impl {:mvn/version "2.13.3"}}

 :aliases {:test {:extra-paths ["../../components/user/test"
                                "../../bases/user-api/test"]
                  :extra-deps  {}}

           :aot     {:extra-paths ["classes"]
                     :main-opts   ["-e" "(compile,'se.example.user-api.core)"]}

           :uberjar {:extra-deps {uberdeps {:mvn/version "0.1.10"}}
                     :main-opts  ["-m" "uberdeps.uberjar"
                                  "--aliases" "aot"
                                  "--main-class" "se.example.user_api.core"]}}}
```

Add the `user-s` alias for the `user-service` in `./deps.edn`:
```
{:polylith {...
            :project-to-alias {...
                               "user-service" "user-s"}
```

#### 5. Create a build script for `user-service`.
- [x] Make it executable.
- [x] Execute it.

Create this file:
```sh
example
├── scripts
│   └── build-user-service-uberjar.sh
```

...with this content:
```sh
#!/usr/bin/env bash
./build-uberjar.sh user-service
```

Create an uberjar for the `user-service`:
```
cd scripts
chmod +x build-user-service-uberjar.sh
./build-user-service-uberjar.sh
```

Puhh, that should be it! Now let's test if it works.

Execute this from the workspace root in a separate terminal:
```
cd projects/user-service/target
java -jar user-service.jar
```

It should output:
```
server started: http://127.0.0.1:2104
```

Now when we have a running service, we could test if we can call it from the REPL.
We activated the `remote` profile in our IDE earlier, which made the `user-remote` component active.
Note that this only instructs the IDE to treat `user-remote` as source code:

<img src="images/component-dirs.png" width="17%">

...but it **doesn't load** its source code into the REPL!

We can verify this by adding this code to `development/src/dev/lisa.clj`:
```
(ns dev.lisa
  (:require [se.example.user.interface :as user]))

(user/hello "Lisa")
``` 
...and if we execute the `hello` function, we still get:
```
"Hello Lisa!!"
```

Remember that we set the REPL configuration to "dev,test,+default"
which loads the `user` component into the REPL every time we start or restart the REPL. 
This is the recommended way of configuring the default REPL, by selecting the "simple" components that
communicate with each other using direct function calls.
Because of this, we should keep the "dev,test,+default" configuration as it is.

What we can do is to create another REPL configuration, e.g. "REPL prod", and set `Aliases` to "dev,test,+remote".
This REPL will use the `user-remote` component and can be used to "emulate" a production like environment.

But let's continue with the REPL we already have and let's see if we can switch to `user-remote` without restarting the REPL. 
Open the `interface` namespace of the `user-remote` component and select `Tools > REPL > Load file in REPL`.
This will replace the `user` implementaton with the `user-remote` component, which works because both
live in the same `se.example.user` namespace, which is also their interface (`user`).

If we execute the `hello` function agan, we should get:
```
Hello Lisa - from the server!!
```

Now, let's continue with our example. Execute this from the other terminal
(the one that we didn't start the server from):
```
cd ../../../projects/command-line/target
java -jar command-line.jar Lisa
```
```
Hello Lisa - from the server!!
```

Wow, that worked too!

Now execute the `info` command (`+` inactivates all profiles, and makes the `default` profile visible):
```
cd ../../..
poly info +
```

...and compare it with the target design:
| | |
|:-|:-| 
|<img src="images/profile-info-1.png" width="80%"> | <img src="images/target-architecture.png"> |

Looks like we got everything right! 

The profile flags, `xx`, follows the same pattern as for
bricks and projects except that the last `Run the tests` flag is omitted.

This example was quite simple, but if our project is more complicated, we may want to manage state during 
development with a tool like [Mount](https://github.com/tolitius/mount) or we could create our own 
helper functions that we put in the `dev.lisa` namespace, which can help us switch profiles
by using a library like [tools.namespace](https://github.com/clojure/tools.namespace).

If we want to switch profile when running a command, we need to pass them in, e.g.:
```sh
poly info +remote
```
<img src="images/profile-info-2.png" width="43%">

Now the `remote` profile is included in the `development` project and listed after `active profiles`.

It's possible to give more than one profile:
```
poly info +default +remote
```
<img src="images/profile-info-3.png" width="55%">

The tool complains and doesn't like that we just included both `user` and `user-remote` in the `development` 
project!

The profiles can also contain libraries and paths to projects, but right now we have no such paths
and therefore all profiles are marked with `--` in the project section.

Now when we are finished with our example system, it could be interesting to see how many lines of code
each brick and project consists of. This can be done by passing in `:loc`:
```
poly info :loc
```
<img src="images/profile-info-4.png" width="51%">

Each project summarises the number of lines of code for each brick it contains.
The `loc` column counts the number of lines of codes under the `src` directory,
while `(t)` counts for the `test` directory.

Our projects are still quite small, but they will eventually reach 1000 lines of code,
and when that happens we may want to change the thousand delimiter in `~/.polylith/config.edn`
which is set to `,` by default.

Let's run all the tests to see if everything works:
```
poly test :project
```
<img src="images/profile-test.png" width="85%">

It worked!

## Dependencies

To explain dependencies, we will use the
[RealWorld example app](https://github.com/furkan3ayraktar/clojure-polylith-realworld-example-app/tree/clojure-deps).

Start by cloning the project by executing these commands from outside the `example` workspace, 
e.g. the parent folder of our `example` workspace:

```
clone-from-here
├── example
└── clojure-polylith-realworld-example-app
```

```sh
git clone git@github.com:furkan3ayraktar/clojure-polylith-realworld-example-app.git
cd clojure-polylith-realworld-example-app
```

Before we continue, it may be worth mentioning that most commands, except the [test](#test) command,
can be executed from other workspaces by giving `ws-dir`, e.g.:
```
poly check ws-dir:../example
``` 

Another way of giving the `ws-dir` is to pass in `::` which will set it to the first parent directory that contains
a `deps.edn` workspace file, e.g.:
```
cd projects/realworld-backend
poly info ::
``` 

...which in this case is the same as:
```
poly info ws-dir:../..
```

Now, let's tag the RealWorld application as stable (which will only affect our local clone):
```
cd ../..
git tag -f stable-lisa
```

```
poly info
```
<img src="images/realworld-info.png" width="30%">

Now we have some bricks to play with! 

Let's list all dependencies by executing the [deps](#deps-project) command:
```
poly deps
```
<img src="images/realworld-deps-interfaces.png" width="27%">

This lists all dependencies in the workspace.
Notice the yellow color in the headers. They are yellow because components and bases only depend on `interfaces`. 

If we read the diagram horizontally, we can see that the `article` component uses the `database`, 
`profile` and `spec` interfaces.
If we read it vertically, we can see that the `article` is used by the `comment` and `rest-api` bricks.

This is also what is shown if we specify `article` as brick:
```
poly deps brick:article
```
<img src="images/realworld-deps-interface.png" width="30%">

To list the component dependencies, we need to specify a `project`:
```
poly deps project:rb
```
<img src="images/realworld-deps-components.png" width="30%">

Now, all the headers are green, and that is because all the implementing components are known
within the selected project.
The `+` signs show indirect dependencies. An example is the `article` component
that uses `log` indirectly:  article > database > log.

> Tip: If the headers and the "green rows" don't match, it may indicate that we have
unused components that can be removed from the project.

We can also show dependencies for a specific brick within a project:
```
poly deps project:rb brick:article
```
<img src="images/realworld-deps-component.png" width="30%">

## Libraries

Libraries are specified in `deps.edn` under each project:
- The development project: `./deps.edn` > `:aliases` > `:dev` > `:extra-deps`.
- Other projects: `projects/PROJECT-DIR` > `deps.edn` > `:deps`.

To list all libraries used in the workspace, execute the [libs](#libs) command:
```
poly libs
```
<img src="images/realworld-lib-deps.png" width="60%">

Libraries can be specified in three different ways in `tools.deps`:

| Type  | Description |
|:------|:------------------------------------------------------|
| Maven | As a [Maven](https://maven.apache.org/) dependency. Example: `clj-time/clj-time {:mvn/version "0.15.2"}` where the key is the Maven `groupId/artifactId`. Those dependencies are stored locally in the `~/.m2/repositories` directory. |
| Local | As a local dependency. Example: `clj-time {:local/root "/local-libs/clj-time-0.15.2.jar"}` where the key is an arbitrary identifier. A local dependency is a path to a locally stored file. |
| Git   | As a [Git](https://git-scm.com/) dependency. Example: `clj-time/clj-time {:git/url "https://github.com/clj-time/clj-time.git", :sha "d9ed4e46c6b42271af69daa1d07a6da2df455fab"}` where the key must match the path for the library in `~/.gitlibs/libs` (to be able to calculate the `KB` column). |
 
The KB column shows the size of each library in kilobytes. If you get the key path wrong or if the library
hasn't been downloaded yet, then it will be shown as blank.
   
Library dependencies are specified per project and the way the tool figures out what library each 
brick uses is to look in `:ns-to-lib` in `./deps.edn`, e.g:

```clojure
            :ns-to-lib {clj-time              clj-time
                        clj-jwt               clj-jwt
                        clojure               org.clojure/clojure
                        clojure.java.jdbc     org.clojure/java.jdbc
                        compojure             compojure/compojure
                        crypto.password       crypto-password
                        environ               environ
                        honeysql              honeysql
                        slugger               slugger
                        ring.logger           ring-logger-timbre
                        ring.middleware.json  ring/ring-json
                        spec-tools            metosin/spec-tools
                        taoensso.timbre       com.taoensso/timbre}}
```
 
This map specifies which namespace maps to which library, and needs to be manually populated.
The same library can occur more than once as long as the namespaces are unique.
By populating `:ns-to-lib`, the [libs](#libs) command will be able to show library usage per brick.
Another advantage is that we will receive a validation error from the [check](#check) and [info](#info) commands,
if we forget to add a library to an environment, which is much nicer than trying to understand 
the stack traces that we would otherwise get when we run the tests!

The way the algorithm works is that it takes all the namespaces and sort them in reverse order.
Then it tries to match each namespace against that list from top to down and takes the first match.

Let's say we have this mapping:
```clojure
:ns-to-lib {com.a      library-a
            com.a.b    library-b
            com.a.b.c  library-c}
```

...then it will return the first matching namespace going from the top down:
```
namespace   library
---------   ---------
com.a.b.c   library-c
com.a.b     library-b
com.a       library-a
```  
For example:
- If we compare with the `com.a.x.y` namespace, it will match against `com.a` and return `library-a`.  
- If we compare with the `com.a.b.x` namespace, it will match against `com.a.b` and return `library-b`.

If we have a lot of libraries, we can choose a more compact format by setting `:compact-views` to `#{"libs"}` in `./deps.edn`:

<img src="images/realworld-lib-deps-compact.png" width="52%">

## Context

The component interfaces bring context to the development experience.

Object oriented languages give us context by using objects. Let’s say we work in an 
object oriented language and that we want to save the object `userToBeSaved`. 
If we type `userToBeSaved` followed by a `.`, the intellisense in the IDE will show us a 
list of available methods for that object, for example `persist`:
```ruby
userToBeSaved.persist(db)
```

...or if implemented as a service:
```ruby
userService.persist(db, userToBeSaved)
```

With Polylith we get the same level of support from the IDE
by first importing the `user` interface and then typing:
```clojure
(user/
```

...now the IDE will list all available functions in the `user` interface and one of them would be `persist!`:
```clojure
(user/persist! db user-to-be-saved)
```

## Naming

Every time we create an `interface`, `component`, `base`, `project` or `workspace`,
we need to come up with a good name.
Finding good names is one of the hardest and most important thing in software.
Every time we fail to find a good name, it will make the system harder to reason about and change.

The components are the core of Polylith, so let's start with them.
If a component does **one thing** then we can name it based on that, e.g.
`validator`, `invoicer` or `purchaser`. Sometime a component operates around a concept
that we can name it after, e.g.: `account` or `car`. This can be an alternative if the component
does more than one thing, but always around that single concept.

If the component's main responsibility is to simplify access to a third party API, 
then suffixing it with `-api` is a good pattern, like `aws-api`.

If we have two components that share the same interface, e.g. `invoicer`, 
where the `invoicer` component contains the business logic, while the other component only delegates
to a service that includes the `invoicer` component, then we can name the component
that does the remote call, `invoicer-remote`.

If we have found a good name for the component, then it's generally a good idea to keep the same name for
the interface, which is also the default behaviour when a component is created, e.g.:

```
poly create component name:invoicer
```
...which is the same as:
```
poly create component name:invoicer interface:invoicer
``` 

Bases are responsible for exposing a public API and delegating the incoming calls to components.
A good way to name them is to start with what they do, followed by the type of the API.
If it's a REST API that takes care of invoicing, then we can name it `invoicer-rest-api`.
If it's a lambda function that generates different reports, then `report-generator-lambda` can be a 
good name.

Projects (development excluded) represent the deployable artifacts, like services. Those artifacts
should, if possible, be named after what they are, like `invoicer` or `report-generator`.

## Configuration

The workspace configuration is stored in `./workspace.edn` and defines the following keys:

| Key                    | Description
|:-----------------------|:---------------------------------------------------------------------------------------------|
| :top-namespace         | The workspace top namespace. If changed, the source code has to be changed accordingly. |
| :interface-ns          | The default value is `interface`. If changed, the source code has to be changed accordingly. |
| :default&#8209;profile&#8209;name  | The default value is `default`. If changed, the `+default` alias in `./deps.edn` has to be renamed accordingly. |
| :release-tag-pattern   | The default value is `v[0-9]*`. If changed, old tags may not be recognised. |
| :stable-tag-pattern    | The default value is `stable-*`. If changed, old tags may not be recognised. |
| :compact-views         | The default value is `#{}`. If set to `#{"libs"}`, then the `libs` diagram will be shown in a more compact format. Only "libs" is supported at the moment. |
| :projects              | If the `development` key is missing, `{"development" {:alias "dev", :test []}` will be added. |

Only the `:top-namespace` attribute is mandatory, all other attributes will use their default values.

Settings that are unique per developer/user are stored in `~/.polylith/config.edn`:

| Key                  | Description
|:---------------------|:---------------------------------------------------------------------------------------------|
| :thousand&#8209;separator  | Set to "," by default (when first created). |
| :color-mode          | Set to "none" on Windows, "dark" on other operating systems (when first created). Valid values are "none", "light" and "dark", see the [color](#color) section. Can be overridden, e.g.: `poly info color-mode:none`. |
| :empty-character     | Set to "." on Windows, "·" on other operating systems (when first created). Used by the [deps](#deps) and [libs](#libs) commands. |
| :m2-dir              | If omitted, the `.m2` directory will be set to USER-HOME/.m2. Used by the [libs](#libs) command to calculate file sizes (KB). |

If `~/.polylith/config.edn` doesn't exists, it will be created the first time the [create workspace](doc/commands.md#create-workspace) command is executed, e.g.:

```
{:color-mode "dark"
 :thousand-separatpr ","
 :empty-character "·"}
```

## Workspace state

There is a way to view all configuration that is used by the tool, and that is to execute the [ws](#ws) command
(here, against the `example` workspace):
```
poly ws get:settings
```
```clojure
{:active-profiles #{"default"},
 :color-mode "dark",
 :compact-views #{},
 :default-profile-name "default",
 :empty-char "·",
 :interface-ns "interface",
 :m2-dir "/Users/tengstrand/.m2",
 :ns-to-lib {"slacker" "slacker"},
 :profile-to-settings {"default" {:base-names [],
                                  :component-names ["user"],
                                  :lib-deps {},
                                  :paths ["components/user/src"
                                          "components/user/resources"
                                          "components/user/test"],
                                  :project-names []},
                       "remote" {:base-names [],
                                 :component-names ["user-remote"],
                                 :lib-deps {},
                                 :paths ["components/user-remote/src"
                                         "components/user-remote/resources"
                                         "components/user-remote/test"],
                                 :project-names []}},
 :project-to-alias {"command-line" "cl",
                    "development" "dev",
                    "user-service" "user-s"},
 :release-tag-pattern "v[0-9]*",
 :stable-tag-pattern "stable-*",
 :thousand-sep ",",
 :top-namespace "se.example",
 :user-config-file "/Users/tengstrand/.polylith/config.edn",
 :user-home "/Users/tengstrand",
 :vcs "git",
 :version "0.1.0-alpha9",
 :ws-schema-version {:breaking 0, :non-breaking 0}}
```

If we are only interested in a specific element in this structure, we can dig deeper into it:
```
poly ws get:settings:profile-to-settings:default
```

...which outputs:
```clojure
{:lib-deps {},
 :paths ["components/user/src"
         "components/user/resources"
         "components/user/test"]}
```

If we execute `poly ws` without any arguments, it will view the whole workspace as plain data (a hash map).
This data structure is produceed by the tool itself and is used by all the commands internally.
The commands only operate on this hash map and are not performing any io operations,
such as touching the disk or executing git commands. Instead, everything is prepared so that all commands can
be executed in memory.

This will not only simplify the code of the tool itself but it also gives us, as a user of the tool,
a way to explore the complete state of the workspace.

A good way to start digging into this data structure is to list all its keys:
```
poly ws get:keys
```
```clojure
[:bases
 :changes
 :components
 :projects
 :interfaces
 :messages
 :name
 :paths
 :settings
 :user-input
 :ws-dir
 :ws-reader]

```
To list the components, type:
```
poly ws get:components:keys
```
```clojure
["user" "user-remote"]
```

To show the `user` component:
```
poly ws get:components:user
```
```clojure
{:interface {:definitions [{:name "hello",
                            :parameters [{:name "name"}],
                            :type "function"}],
             :name "user"},
 :interface-deps [],
 :lib-deps {},
 :lib-deps-test {},
 :lib-imports-src [],
 :lib-imports-test ["clojure.test"],
 :lines-of-code-src 9,
 :lines-of-code-test 7,
 :name "user",
 :namespaces-src [{:file-path "/Users/tengstrand/source/polylith/example/example/components/user/src/se/example/user/interface.clj",
                   :imports ["se.example.user.core"],
                   :name "interface",
                   :namespace "se.example.user.interface"}
                  {:file-path "/Users/tengstrand/source/polylith/example/example/components/user/src/se/example/user/core.clj",
                   :imports [],
                   :name "core",
                   :namespace "se.example.user.core"}],
 :namespaces-test [{:file-path "/Users/tengstrand/source/polylith/example/example/components/user/test/se/example/user/interface_test.clj",
                    :imports ["clojure.test" "se.example.user.interface"],
                    :name "interface-test",
                    :namespace "se.example.user.interface-test"}],
 :type "component"}
```
There is a way to store the workspace state to a file, and that is to give the `out` parameter, e.g.:
```
poly ws out:ws.edn
```

An alternative way to reach the same result is to turn off the coloring and pipe to `ws.edn`:
```
poly ws color-mode:none > ws.edn
```

This can be used to share the workspace state with others without sending them the whole workspace including the code.
To load this workspace, they have to give the `ws-file` parameter, e.g.:

```
poly info ws-file:ws.edn
``` 

This will give the exact same output as if we execute `poly info` on the machine that created `ws.edn`.
All commands except `test` and `create` can be executed when `ws-file` is given.

Here is an example where we inspect the arguments used to produce the file:
```
poly ws get:old-user-input:args ws-file:ws.edn
``` 

...which returns:
```clojure
["ws" "out:ws.edn"]
```

The `old-user-input` key is added when `ws-file` is given.

## Git hook

We can ensure that we don't push code that puts the workspace in an invalid state,
by adding a [git hook](https://git-scm.com/book/en/v2/Customizing-Git-Git-Hooks) to our workspace,
that executes the [check](doc/commands.md#check) command.

To make this work, all developers should add `.git/hooks/commit-msg` to the root of the workspace
on their local disk with the following content, e.g.:

```
#!/usr/bin/env bash

exec /usr/bin/java -jar /usr/local/polylith/poly.jar check color-mode:none ws-dir:PATH-TO-WORKSPACE-DIRECTORY

if [[ $? -ne 0 ]] then
  exit 1
fi
```

Replace `PATH-TO-WORKSPACE-DIRECTORY` with the path to the workspace root.

## Mix languages

Polylith allows us to run multiple languages side by side where each language lives in its own workspace.
This will work especially well if we run different languages on top of the same platform, e.g. the JVM
as for this tool (see list of [JVM languages](https://en.wikipedia.org/wiki/List_of_JVM_languages)).

Let's say we have the languages A, B and C. The first thing to remember is to have different
names of the top namespace for each language, so that we don't run into name conflicts.
We would end up with top namespaces like: `com.mycompany.a`, `com.mycompany.b` and `com.mycompany.c`.

Each language will have its own workspace and will compile all components and bases 
into one big jar like `a.jar`, `b.jar` or `c.jar`, that can then be used by other languages.

## Colors

When we created the `example` workspace, the file `~/.polylith/config.edn` was also created:
```
{:color-mode "dark"
 :thousand-sep ","
 :empty-character "·"}
```

For Windows systems the `color-mode` is set to `none` and for all other systems, `dark` will be used as default.
Valid values are: `none`, `light` and `dark`.

In this documentation we have used the `dark` color schema, but we can switch to `light`
by giving the `color-mode` parameter (or by updating `~/.polylith/config.edn`):
```
poly info color-mode:light
```
<img src="images/color-info.png" width="40%">

...everything suddenly looks much brighter!
The only difference between "light" and "dark" is that they use different [codes](https://github.com/polyfy/polylith/tree/master/components/util/src/polylith/clj/core/util/colorizer.clj) for grey.

If we switch back to dark background and select `none`:
```
poly info color-mode:none
```
<img src="images/color-none.png" width="40%">

...things are now displayed without colors. 

To refresh our memory, this is what it looked like using the `dark` color schema:

<img src="images/profile-info-2.png" width="40%">

If you want to use the same colors in your terminal, here they are:<br>
<img src="images/polylith-colors.png" width="50%">

If the colors (f8eeb6, bfefc5, 77bcfc, e2aeff, cccccc, 24272b, ee9b9a) looks familiar to you, it's because they are 
more or less stolen from the [Borealis](https://github.com/Misophistful/borealis-cursive-theme) color schema!
This color schema gives a really pleasant user experience when used from the text editor / IDE.

Happy coding!

## Contact

Feel free to contact me:<br>
&nbsp;&nbsp;Twitter: @jtengstrand<br>
&nbsp;&nbsp;Email: info[at]polyfy[dot]com

You can also get in touch with us in the [Polylith forum](https://polylith.freeflarum.com) 
or on [Slack](https://clojurians.slack.com/archives/C013B7MQHJQ).

## License

Distributed under the [Eclipse Public License 1.0](http://opensource.org/licenses/eclipse-1.0.php), the same as Clojure.

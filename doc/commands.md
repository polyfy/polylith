
The individual help texts listed here are taken from the built-in `help` command.

**Commands**
- [check](#check)
- [create](#create)
  - [create component](#create-component)
  - [create base](#create-base)
  - [create project](#create-project)
  - [create workspace](#create-workspace)
- [deps](#deps)
  - [deps :bricks](#deps-bricks)
  - [deps :brick](#deps-brick)
  - [deps :project](#deps-project)
  - [deps :project :brick](#deps-project-brick)
- [diff](#diff)
- [info](#info)
- [libs](#libs)
- [migrate](#migrate)
- [shell](#shell)
- [switch-ws (shell)](#switch-ws)
- [tap (shell)](#tap)
- [test](#test)
- [ws](#ws)

To list all available commands, type:
```
poly help
```

```
  Poly 0.2.16-alpha-issue247 (2022-10-27) - https://github.com/polyfy/polylith

  poly CMD [ARGS] - where CMD [ARGS] are:

    check                       Checks if the workspace is valid.
    create E name:N [ARG]       Creates a component, base, project or workspace.
    deps [project:P] [brick:B]  Shows dependencies.
    diff                        Shows changed files since last stable point in time.
    help [C] [ARG]              Shows this help or help for specified command.
    info [ARGS]                 Shows a workspace overview and checks if it's valid.
    libs                        Shows all libraries in the workspace.
    migrate                     Migrates the workspace to the latest format.
    shell                       Starts an interactive shell.
    test [ARGS]                 Runs tests.
    version                     Shows current version of the tool.
    ws [get:X]                  Shows the workspace as data.

  From the shell:

    switch-ws ARG  Switches to specified workspace.
    tap [ARG]      Opens a portal window that outputs tap> statements.
    exit           Exits the shell.
    quit           Quits the shell.

  The ws-dir and ws-file parameters are replaced by switch-ws when executing commands
  from the shell.

  If ws-dir:PATH is passed in as an argument, where PATH is a relative
  or absolute path, then the command is executed from that directory.
  This works for all commands except 'test'.
  If the 'switch-ws dir:DIR' command has been executed from a shell,
  then ws-dir:DIR will automatically be appended to following commands.

  If ws-file:FILE is passed in, then the workspace will be populated with the content
  from that file. All commands except 'create' and 'test' can be executed with this
  parameter set. The FILE is created by executing the 'ws' command, e.g.:
  'poly ws out:ws.edn'.
  If the 'switch-ws file:FILE' command has been executed from a shell,
  then ws-file:FILE will automatically be appended to following commands.

  If :: is passed in, then ws-dir is set to the first parent directory (or current)
  that contains a 'workspace.edn' config file. The exception is the 'test' command
  that has to be executed from the workspace root.

  If skip:PROJECTS is passed in, then the given project(s) will not be read from disk.
  Both project names and aliases can be used and should be separated by : if more than one.

  If since:SINCE is passed in as an argument, the last stable point in time will be
  used depending on the value of SINCE (or the first commit if no match was found).
  If prefixed with 'previous-', e.g. 'previous-release', then the SHA directly before
  the most recent matching tag of the 'release' pattern will be used:
    stable  -> the latest tag that matches stable-*, defined by
               :tag-patterns > :stable in workspace.edn.
    release -> the latest tag that matches v[0-9]*, defined by
               :tag-patterns > :release in workspace.edn.
    KEY     -> any key in :tag-patterns.
    SHA     -> a git SHA-1 hash (if no key was found in :tag-patterns).

  The color mode is taken from ~/.config/polylith/config.edn but can be overridden by passing
  in color-mode:COLOR where valid colors are none, light, and dark.
  (if the XDG_CONFIG_HOME environment variable is set, that will be used instead of ~/.config)

  Example (shell only):
    switch-ws dir:~/myworkspace
    switch-ws file:../../another/ws.edn
    tap
    tap open
    tap clean
    tap close

  Example:
    poly check
    poly create c name:user
    poly create component name:user
    poly create component name:admin interface:user
    poly create base name:mybase
    poly create project name:myproject
    poly create w top-ns:com.my.company
    poly create workspace name:myws top-ns:com.my.company
    poly create workspace name:myws top-ns:com.my.company branch:master
    poly deps
    poly deps project:myproject
    poly deps brick:mybrick
    poly deps project:myproject brick:mybrick
    poly diff
    poly help
    poly help info
    poly help create
    poly help create component
    poly help create base
    poly help create project
    poly help create workspace
    poly help deps
    poly help deps :project
    poly help deps :brick
    poly help deps :project :brick
    poly info
    poly info :loc
    poly info since:65a7918
    poly info since:head
    poly info since:head~1
    poly info since:stable
    poly info since:release
    poly info since:previous-release
    poly info skip:dev
    poly info skip:dev:myproject
    poly info project:myproject
    poly info project:myproject:another-project
    poly info brick:mycomponent
    poly info brick:mycomponent:mybase
    poly info :project
    poly info :dev
    poly info :project :dev
    poly info :all
    poly info :all-bricks
    poly info ::
    poly info color-mode:none
    poly info ws-dir:another-ws
    poly info ws-file:ws.edn
    poly libs
    poly migrate
    poly shell
    poly test
    poly test :project
    poly test :all-bricks
    poly test :all
    poly test project:proj1
    poly test project:proj1:proj2
    poly test brick:mycomponent
    poly test brick:mycomponent:mybase
    poly test :dev
    poly test :project :dev
    poly test :all-bricks :dev
    poly test :all :dev
    poly version
    poly ws
    poly ws get:keys
    poly ws get:count
    poly ws get:settings
    poly ws get:user-input:args
    poly ws get:user-input:args:0
    poly ws get:settings:keys
    poly ws get:components:keys
    poly ws get:components:count
    poly ws get:components:mycomp:lines-of-code
    poly ws get:settings:vcs:polylith :latest-sha
    poly ws get:settings:vcs:polylith :latest-sha branch:master
    poly ws get:changes:changed-or-affected-projects skip:dev color-mode:none
    poly ws out:ws.edn
```

### check
```
  Validates the workspace.

  poly check

  Prints 'OK' and returns 0 if no errors were found.
  If errors or warnings were found, show messages and return the error code,
  or 0 if only warnings. If internal errors, 1 is returned.

  Error 101 - Illegal dependency on namespace.
    Triggered if a :require statement refers to a component namespace
    other than interface. Examples of valid namespaces:
     - com.my.company.mycomponent.interface
     - com.my.company.mycomponent.interface.subns
     - com.my.company.mycomponent.interface.my.subns

  Error 102 - Function or macro is defined twice.
    Triggered if a function or macro is defined twice in the same namespace.

  Error 103 - Missing definitions.
    Triggered if a def, defn or defmacro definition exists in one component's
    interface but is missing in another component that uses the same interface.

  Error 104 - Circular dependencies.
    Triggered if circular dependencies were detected, e.g.:
    Component A depends on B that depends on A (A > B > A), or A > B > C > A.

  Error 105 - Illegal name sharing.
    Triggered if a base has the same name as a component or interface.
    Projects and profiles can be given any name.

  Error 106 - Multiple components that share the same interfaces in a project.
    Triggered if a project contains more than one component that shares the
    same interface.

  Error 107 - Missing components in project.
    Triggered if a component depends on an interface that is not included in the
    project. The solution is to add a component to the project that
    implements the interface.

  Error 108 - Components with an interface that is implemented by more than one
              component are not allowed for the development project.
    The solution is to remove the component from the development project
    and define the paths for each component in separate profiles
    (including test paths).

  Error 109 - Invalid test runner configuration for some projects.
    The value of the optional :create-test-runner key under [:test] or
    [:projects "some-project-name" :test] in workspace.edn must be either
    nil, :default, or a fully qualified symbol referring to a function on
    the poly tool's classpath, which can take a single argument and must return
    an instance of polylith.clj.core.test-runner-contract.interface/TestRunner.

  Warning 201 - Mismatching parameter lists in function or macro.
    Triggered if a function or macro is defined in the interface for a component
    but also defined in the same interface for another component but with a
    different parameter list.

  Warning 202 - Missing paths in project.
    Triggered if a path in a project doesn't exist on disk.
    The solution is to add the file or directory, or to remove the path.

  Warning 203 - Path exists in both dev and profile.
    It's discouraged to have the same path in both the development project
    and a profile. The solution is to remove the path from dev or the profile.

  Warning 205 - Non top namespace was found in brick.
    Triggered if a namespace in a brick doesn't start with the top namespaces
    defined in :top-namespace in ./workspace.edn.
```

### create
```
  Creates a component, base, project or workspace.

  poly create TYPE [ARGS]
    TYPE = c[omponent] -> Creates a component.
           b[ase]      -> Creates a base.
           p[roject]   -> Creates a project.
           w[orkspace] -> Creates a workspace.

    ARGS = Varies depending on TYPE. To get help for a specific TYPE, type:
             poly help create TYPE

  Example:
    poly create c name:user
    poly create component name:user
    poly create component name:admin interface:user
    poly create base name:mybase
    poly create project name:myproject
    poly create workspace name:myws top-ns:com.my.company
    poly create workspace name:myws top-ns:com.my.company branch:master
```

### create component
```
  Creates a component.

  poly create component name:NAME [interface:INTERFACE]
    NAME = The name of the component to create.

    INTERFACE = The name of the interface (namespace) or NAME if not given.

  Example:
    poly create c name:user
    poly create component name:user
    poly create component name:admin interface:user
```

### create base
```
  Creates a base.

  poly create base name:NAME
    NAME = The name of the base to create.

  Example:
    poly create b name:mybase
    poly create base name:mybase
```

### create project
```
  Creates a project.

  poly create project name:NAME
    NAME = The name of the project to create.

  Example:
    poly create p name:myproject
    poly create project name:myproject
```

### create workspace
```
  Creates a workspace in current directory. If the workspace is created within
  an existing git repo, then that repository will be used. If the workspace is
  created outside a git repo, and the :create flag is passed in, then a new repo
  will be initiated using the 'main' branch, if another branch is not explicitly
  given. In both cases, all the created files and directories will be committed
  to the repo in a single commit with the text 'Workspace created.'.

  If :commit is not passed in, then the repository needs to be initiated manually
  with statements similar to this:
    git init
    git add .
    git commit -m "Workspace created."

  poly create workspace [name:NAME] top-ns:TOP-NAMESPACE [:commit] [branch:BRANCH]
    NAME = The name of the workspace to create, which must be given
           if created outside a git repository. Otherwise it's optional.

    TOP-NAMESPACE = The top namespace, e.g. com.my.company.

    BRANCH = The name of the branch, e.g. master. Default is main.

  Example:
    poly create w name:myws top-ns:com.my.company
    poly create workspace name:myws top-ns:com.my.company
    poly create workspace name:myws top-ns:com.my.company :commit
    poly create workspace name:myws top-ns:com.my.company branch:master
```

### deps
```
  Shows dependencies.

  poly deps [project:PROJECT] [brick:BRICK]
    (omitted) = Show workspace dependencies.
    PROJECT   = Show dependencies for specified project.
    BRICK     = Show dependencies for specified brick.

  To get help for a specific diagram, type: 
    poly help deps ARGS:
      ARGS = :brick           Help for the brick diagram.
             :project         Help for the project diagram.
             :workspace       Help for the workspace diagram.
             :project :brick  Help for the project/brick diagram.

  Example:
    poly deps
    poly deps project:myproject
    poly deps brick:mybrick
    poly deps project:myproject brick:mybrick
```

### deps :brick
```
  Shows dependencies for selected brick.

  poly deps brick:BRICK
    BRICK = The name of the brick to show dependencies for.

  used by  <  user  >  uses
  -------              ----
  payer                util

  In this example, user is used by payer and it uses util itself.
  If a brick or interface ends with '(t)' then it indicatest that
  it's only used from the test context.

  Example:
    poly deps brick:mybrick
```

### deps :project
```
  Shows dependencies for selected project.

  poly deps project:PROJEXT
    PROJECT = The project name or alias to show dependencies for.

         p      
         a  u  u
         y  s  t
         e  e  i
  brick  r  r  l
  --------------
  payer  .  x  t
  user   .  .  x
  util   .  .  .
  cli    x  +  +

  When the project is known, we also know which components are used.

  In this example, payer uses user in the src context, and util only
  in the test context. user uses util, and cli uses payer. The 't'
  means that payer is only used in the test context by user. The +
  signs mark indirect dependencies, while - signs (not present here)
  mark indirect dependencies in the test context. Here the cli base
  depends on user and util, via 'cli > payer > user' and
  'cli > payer > util'. Each usage comes from at least one :require
  statement in the brick.

  Example:
    poly deps project:myproject
```

### deps :workspace
```
  Shows dependencies for the workspace.

  poly deps

         p      
         a  u  u
         y  s  t
         e  e  i
  brick  r  r  l
  --------------
  payer  .  x  t
  user   .  .  x
  util   .  .  .
  cli    x  .  .

  In this example, payer uses user from the src context, and util from
  the test context (indicated by 't'). user uses util and cli uses payer.
  Each usage comes from at least one :require statement in the brick.
```

### deps :project :brick
```
  Shows dependencies for selected brick and project.

  poly deps project:PROJECT brick:BRICK
    PROJECT = The project (name or alias) to show dependencies for.
    BRICK   = The brick to show dependencies for.

  used by  <  user  >  uses
  -------              ----
  payer                util

  In this example, user is used by payer and it uses util itself.
  If a brick ends with '(t)' then it indicatest that it's only used
  from the test context.

  Example:
    poly deps project:myproject brick:mybrick
```

### diff
```
  Shows changed files since the most recent stable point in time.

  poly diff

  Internally, it executes 'git diff SHA --name-only' where SHA is the SHA-1
  of the first commit in the repository, or the SHA-1 of the most recent tag
  that matches the default pattern 'stable-*'.

  Stable points are normally set by the CI server or by individual developers,
  e.g. Lisa, with 'git tag -f stable-lisa'.

  The pattern can be changed in :tag-patterns in workspace.edn.

  The way the latest tag is found is by taking the first line that matches the 'stable-*'
  regular expression, or if no match was found, the first commit in the repository.
    git log --pretty=format:'%H %d'

  Here is a compact way of listing all the commits including tags:
    git log --pretty=oneline
```

### info
```
  Shows workspace information.

  poly info [ARGS]
    ARGS = :loc   -> Shows the number of lines of code for each brick
                     and project.

  In addition to :loc, all the arguments used by the 'test' command
  can also be used as a way to see what tests will be executed.

    stable since: dec73ec | stable-lisa

    projects: 2   interfaces: 3
    bases:    1   components: 4

    active profiles: default

    project       alias  status   dev  admin
    ---------------------------   ----------
    command-line  cl      ---     ---   --
    development   dev     s--     s--   --

    interface  brick    cl    dev  admin
    -----------------   ---   ----------
    payer      payer    s--   st-   --
    user       admin    s--   ---   s-
    user       user *   ---   st-   --
    util       util     s--   st-   --
    -          cli      s--   st-   --

  This example shows a sample workspace. Let's go through each section:

  1. stable since: dec73ec | stable-lisa

     Shows the most recent commit marked as stable, or the last release if 
     since:release or since:previous-release was given, or the first commit
     in the repository if no tag was found, followed by the tag (if found).
     More information can be found in the 'diff' command help.

  2. projects: 2   interfaces: 3
     bases:    1   components: 4

     Shows how many projects, bases, components and interfaces there are
     in the workspace.

  3. active profiles: default

     Shows the names of active profiles. The profile paths are merged into the
     development project. A profiles is an alias in ./deps.edn that starts
     with a +. If no profile is selected, the default profile is automatically
     selected.

     Profiles are activated by passing them in by name (prefixed with '+'), e.g.:
       poly info +admin +onemore

     To deactivate all the profiles, and stop 'default' from being merged into
     the development project, type:
       poly info +

  4. project       alias  status   dev  admin
     ---------------------------   ----------
     command-line  cl      ---     ---   --
     development   dev     s--     s--   --

    This table lists all projects. The 'project' column shows the name
    of the projects, which are the directory names under the 'projects',
    directory except for 'development' that stores its code under the
    'development' directory. If any file within the project directory has
    changed, then it will be marked with a *. If that's not the case, but
    any of the included bricks are changed, then it will be marked with a +.

    The 'deps.edn' config files are stored under each project, except for
    the development project that stores it at the workspace root.

    Aliases are configured in :projects in ./workspace.edn.

    The 'status' column has three flags with different meaning:
      s--  The project has a 'src' directory, e.g.
           'projects/command-line/src'.
      -t-  The project has a 'test' directory, e.g.
           'projects/command-line/test'.
      --x  The project tests (its own) are marked for execution.

    To show the 'resources' directory, also pass in :r or :resources, e.g.
    'poly info :r':
      s---  The project has a 'src' directory, e.g.
            'projects/command-line/src'.
      -r--  The project has a 'resources' directory, e.g.
            'projects/command-line/resources'.
      --t-  The project has a 'test' directory, e.g.
            'projects/command-line/test'
      ---x  The project tests (its own) are marked for execution.

    The dev column has three flags with different meaning:
      s--  The project's 'src' directory, e.g.
           'projects/command-line/src' is added to './deps.edn'
           (or indirectly added as :local/root).
      -t-  The project's 'test' directory, e.g.
           'projects/command-line/test' is added to './deps.edn'
           (or indirectly added as :local/root).
      --x  The project tests are marked for execution from development.

    The last admin column, is a profile:
      s-  The profile contains a path to the 'src' directory, e.g.
          'projects/command-line/src'.
      -t  The profile contains a path to the 'test' directory, e.g.
          'projects/command-line/test'.

    If also passing in :r or :resources, e.g. 'poly info +r':
      s--  The profile contains a path to the 'src' directory, e.g.
           'projects/command-line/src'.
      -r-  The profile contains a path to the 'resources' directory, e.g.
           'projects/command-line/resources'.
      --t  The profile contains a path to the 'test' directory, e.g.
           'projects/command-line/test'.

  5. interface  brick    cl    dev  admin
     -----------------   ---   ----------
     payer      payer    s--   st-   --
     user       admin    s--   ---   st
     user       user *   ---   st-   --
     util       util     s--   st-   --
     -          cli      s--   st-   --

    This table lists all bricks and in which projects and profiles they are
    added to.

    The 'interface' column shows what interface the component has. The name
    is the first namespace after the top namespace, e.g.:
    com.my.company.user.interface

    The 'brick' column shows the name of the brick, in green if a component or
    blue if a base. Each component lives in a directory under the 'components'
    directory and each base lives under the 'bases' directory. If any file for
    a brick has changed since the last stable point in time, it will be marked
    with an asterisk, * (user in this example).

    The changed files can be listed by executing 'poly diff'.

    The next cl column is the command-line project that lives under the
    'projects' directory. Each line in this column says whether a brick is
    included in the project or not.
    The flags mean:
      s--  The project contains a path to the 'src' directory, e.g.
           'components/user/src' (or is indirectly added by a :local/root).
      -t-  The project contains a path to the 'test' directory, e.g.
           'components/user/test' (or is indirectly added by a :local/root).
      --x  The brick is marked to be executed from this project.

    If :r or :resources is also passed in:
      s---  The project contains a path to the 'src' directory, e.g. 
            'components/user/src' (or is indirectly added by a :local/root).
      -r--  The project contains a path to the 'resources' directory, e.g.
            'components/user/resources' (or is indirectly added by a :local/root).
      --t-  The project contains a path to the 'test' directory, e.g.
            'components/user/test' (or is indirectly added by a :local/root).
      ---x  The brick is marked to be executed from this project.

    The next group of columns, dev admin, is the development project with
    its profiles. If passing in a plus with 'poly info +' then it will also show
    the default profile. The flags for the dev project works the same
    as for cl.

    The flags for the admin profile means:
      s-  The profile contains a path to the 'src' directory, e.g.
          'components/user/src'.
      -t  The profile contains a path to the 'test' directory, e.g.
          'components/user/test'

  It's not enough that a path has been added to a project to show an 'x',
  the file or directory must also exist.

  If any warnings or errors were found in the workspace, they will be listed at
  the end, see the 'check' command help, for a complete list of validations.

  Example:
    poly info
    poly info :loc
    poly info since:release
    poly info since:previous-release
    poly info project:myproject
    poly info project:myproject:another-project
    poly info brick:mycomponent
    poly info brick:mycomponent:mybase
    poly info :project
    poly info :dev
    poly info :project :dev
    poly info :all
    poly info :all-bricks
    poly info ws-dir:another-ws
    poly info ws-file:ws.edn
```

### libs
```
  Shows all libraries that are used in the workspace.

  poly libs [:all] [:compact]
    :all     = View all bricks, including those without library dependencies.
    :compact = Show the table in a more compact way.
                                                                                      u  u
                                                                                      s  t
                                                                                      e  i
    library                       version   type      KB   cl   dev  default  admin   r  l
    ----------------------------------------------------   --   -------------------   ----
    antlr/antlr                   2.7.7     maven    434   x     x      -       -     .  x
    clj-time                      0.15.2    maven     23   x     x      -       -     x  .
    org.clojure/clojure           1.10.1    maven  3,816   x     x      -       -     .  .
    org.clojure/tools.deps.alpha  0.12.985  maven     46   x     x      -       -     .  .

  In this example we have four libraries used by the cl and dev projects.
  If any of the libraries are added to the default or admin profiles, they will appear
  as an x in these columns. Remember that src and test sources live together in a profile,
  which is fine because they are only used from the development project.

  The x for the cl and dev columns says that the library is part of the src scope.
  If a library is only used from the test scope, then it is marked with a 't'. A library
  used in the test scope, can either be specified directly by the project itself via
  :aliases > :test > :extra-deps or indirectly via included bricks in :deps > :local/root
  which will be picked up and used by the 'test' command.

  The x in the user column, tells that clj-time is used by that component
  by having it specified in its 'deps.edn' file as a src dependency.
  If a dependency is only used from the test scope, then it will turn up as a t.

  Libraries can also be selected per project and it's therefore possible to have
  different versions of the same library in different projects (if needed).
  Use the :override-deps key in the project's 'deps.edn' file to explicitly set
  a version for one or several libraries in a project.

  The 'type' column says in what way the dependency is included:
   - maven, e.g.: clj-time/clj-time {:mvn/version "0.15.2"}
   - local, e.g.: clj-time {:local/root "/local-libs/clj-time-0.15.2.jar"}
   - git,   e.g.: clj-time/clj-time {:git/url "https://github.com/clj-time/clj-time.git"
                                     :sha     "d9ed4e46c6b42271af69daa1d07a6da2df455fab"}

  The KB column shows the size in kilobytes, which is the size of the jar
  file for Maven and Local dependencies, and the size of all files in the
  ~/.gitlibs/libs/YOUR-LIBRARY directory for Git dependencies.
```

### migrate
```
  Migrates a workspace to the latest version.

  poly migrate

  If the workspace hasn't been migrated already, then this command will create a new
  ./workspace.edn file + a deps.edn file per brick. All project deps.edn files will be
   updated. The libraries in each project's deps.edn file will be sorted, so it can be
   an idea to manually change that order and put bricks first followed by the libraries.

  The migration tool will use the :ns-to-lib key to figure out what libraries are
  used in each brick. After the migration, it's recommended to go through all the
  bricks and make sure that all libraries it uses are also specified in each brick's
  deps.edn file.

  Continue by updating each project's deps.edn file and remove libraries that are
  already indirectly included by bricks (via :local/root).
  The paths in ./deps.edn is left untouched and the reason is that the :local/root
  syntax is not supported by all IDE's.

  Starting from version 0.2.0-alpha10, the tool supports specifying dependencies per
  brick in its own deps.edn files. Workspace specific config is stored in ./workspace.edn
  instead of the :polylith key in ./deps which was the case prior to this version.
```

### shell
```
  poly [shell]

  Starts an interactive shell with the name of the selected workspace, e.g.:
    myworkspace$>

  From here we can execute any poly command, e.g.:
    myworkspace$> info

  We can also use the built in autocomplete, e.g.:
    myworkspace$> i

  ...and when pressing the <tab> key, the 'i' is completed to 'info'.

  This works for both commands and arguments, and is context sensitive.
  If we for example type:
    myworkspace$> deps brick:mybrick project:

  ...and press <tab>, it will only suggest projects that include 'mybrick'.

  Parameters that start with a : can be selected by just typing their name,
  e.g. 'l' will select ':loc'. To distinguish between 'project:PROJECT' and
  ':project' we need to type ':p' to select ':project'.

  From the shell we also have access to these commands:
    switch-ws ARG  Switches to selected workspace.
    tap [ARG]      Opens (or closes/cleans) a portal window that outputs tap> statements.
    exit           Exits the shell.
    quit           Quits the shell.

  An alternative way of exiting the shell is by pressing <ctrl>+C or <ctrl>+D.
```

### switch-ws
```
  Selects which workspace to be used by commands executed from the shell.
  This replaces the use of ws-dir:DIR and ws-file:FILE, which will be appended
  automatically when executing commands, if dir:DIR or file:FILE is given.

  switch-ws ARG
    ARG = dir:DIR    Switches to the selected workspace directory.
                     The prompt will be prefixed with 'dir:' to show this.
          file:FILE  Switches to the workspace specified in the selected file,
                     created by something like 'poly ws out:ws.edn'.
                     The prompt will be prefixed with 'file:' to show this.

  Example:
    switch-ws dir:~/myworkspace
    switch-ws file:../../another/ws.edn
```
### tap
```
  Opens (or closes/cleans) a portal window (https://github.com/djblue/portal)
  where tap> statements are sent to. This command is used from the shell and
  is mainly used internally when developing the poly tool itself.

  tap [ARG]
    ARG = (omitted)  Opens a portal window.
          open       Opens a portal window.
          close      Closes the portal window
          clear      Clears the portal window

  Example:
    tap
    tap open
    tap clean
    tap close
```

### test
```
  Executes brick and/or project tests.

  poly test [ARGS]

  The brick tests are executed from all projects they belong to except for the development
  project (if not :dev is passed in):

  ARGS              Tests to execute
  ----------------  -------------------------------------------------------------
  (empty)           All brick tests that are directly or indirectly changed.

  :project          All brick tests that are directly or indirectly changed +
                    tests for changed projects.

  :all-bricks       All brick tests.

  :all              All brick tests + all project tests (except development).


  To execute the brick tests from the development project, also pass in :dev:

  ARGS              Tests to execute
  ----------------  -------------------------------------------------------------
  :dev              All brick tests that are directly or indirectly changed,
                    executed from all projects (development included).

  :project :dev     All brick tests that are directly or indirectly changed,
                    executed from all projects (development included) +
                    tests for changed projects (development included).

  :all-bricks :dev  All brick tests, executed from all projects
                    (development included).

  :all :dev         All brick tests, executed from all projects
                    (development included) + all project tests
                    (development included).

  Projects can also be explicitly selected with e.g. project:proj1 or
  project:proj1:proj2.

  We can also specify which bricks to include, by listing them like this:
  brick:mycomponent:another-component:mybase

  Example:
    poly test
    poly test :project
    poly test :all-bricks
    poly test :all
    poly test project:proj1
    poly test project:proj1:proj2
    poly test brick:mycomponent
    poly test brick:mycomponent:mybase
    poly test :dev
    poly test :project :dev
    poly test :all-bricks :dev
    poly test :all :dev

  The poly tool's default test runner will discover clojure.test tests from the "/test"
  directories of bricks and projects, and execute them using clojure.test/run-tests.

  Alternative test runners can also be used by referring to their constructors
  in workspace.edn:

  {;; To use it as the default test runner for the workspace
   :test {:create-test-runner my.test-runner/create}

   :projects
   {
    ;; To only use it for specific projects
    "foo" {:test {:create-test-runner my.test-runner/create}}

    ;; To revert to poly's built-in default test runner only for specific projects
    "bar" {:test {:create-test-runner :default}}

    ;; To use multiple test runners invoked the specified order
    "baz" {:test {:create-test-runner [my.linter/create :default my.extra/create]}}
    }
   }

  This requires that my.test-runner/create is available on the classpath of the
  poly tool, which is easiest to achieve by running poly as a dependency.

  The docstring of polylith.clj.core.test-runner-contract.interface/TestRunner
  contains details on how to implement a custom test runner and a constructor.

  The poly tool's default test runner is also implemented this way and can be referred to
  as an example; see polylith.clj.core.clojure-test-test-runner.interface/create.

  Refer to the Polylith documentation for more information about custom test runners.
```

### ws
```
  Prints or writes the workspace as data.

  poly ws [get:ARG] [out:FILE] [:latest-sha] [branch:BRANCH]
    ARG = keys  -> Lists the keys for the data structure:
                   - If it's a hash map, it returns all its keys.
                   - If it's a list and its elements are hash maps,
                     it returns a list with all the :name keys.

          count -> Counts the number of elements.

          KEY   -> If applied to a hash map, it returns the value of the KEY.
                   If applied to a list of hash maps, it returns the hash map with
                   a matching :name. Projects are also matched against :alias.

          INDEX -> A list element can be looked up by INDEX.

          Several ARG keys can be given, separated by colon.
          Every new key goes one level deeper into the workspace data structure.

    FILE = Writes the output to the specified FILE. Will have the same effect
           as setting color-mode:none and piping the output to a file.

    :latest-sha = if passed in, then settings:vcs:polylith:latest-sha will be set.
                  If A branch is given, e.g., branch:master, then the latest sha will be
                  retrieved from that branch.

  Example:
    poly ws
    poly ws get:keys
    poly ws get:count
    poly ws get:settings
    poly ws get:user-input:args
    poly ws get:user-input:args:0
    poly ws get:settings:keys
    poly ws get:components:keys
    poly ws get:components:count
    poly ws get:components:mycomp:lines-of-code
    poly ws get:settings:vcs:polylith :latest-sha
    poly ws get:settings:vcs:polylith :latest-sha branch:master
    poly ws out:ws.edn
    poly ws color-mode:none > ws.edn
```


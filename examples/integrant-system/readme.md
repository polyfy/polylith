# Polylith Systems

## Polylith + Integrant

This example demonstrates a basic setup of a stateful system (a Polylith `base`)
handled by the [Integrant](https://github.com/weavejester/integrant) library.

### System Components

The most frequently asked system was taken as an illustrative example. It uses
both "stateful" and "stateless" components to work with a traditional database,
in this case PostgreSQL. By "stateful" we mean components that are part of the
Integrant system (used at runtime) and that may also have Polylith counterparts
(used at build time). By "stateless" we mean regular Polylith components.

The minimal set of system components:

| Component     | Polylith      | Integrant                       | Description                                                                                                                                                                               |
|---------------|---------------|---------------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Config        | `config`      | `:integrant.system/config`      | A "stateful" component encapsulating the system runtime configuration. Every system should start with this one. Here we have it for completeness and keep its implementation dead simple. |
| Embedded DB   | `embedded-pg` | `:integrant.system/embedded-pg` | A "stateful" component which should be divided into two parts along the boundary between the component and the Integrant system that merely prepares arguments and calls its methods.     |
| DataSource    | n/a           | `:integrant.system/data-source` | A "stateful" component which is only required at runtime (to be started and stopped properly), i.e. lacks a Polylith counterpart.                                                         |
| DB Operations | `pg-ops`      | n/a                             | A regular "stateless" component whose methods are parametrized by the required system state (e.g. `data-source`) or its derivatives.                                                      |

### Example Author

Kudos to [Mark Sto](https://github.com/marksto).

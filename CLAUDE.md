# Clojure development

This repo is the Polylith tool itself (organized as a Polylith workspace: `bases/`, `components/`, `projects/`).

## Fast feedback loop (use this while working, not just before delivery)

- For evaluating code interactively: connect to a running nREPL started with the `dev` and `test` aliases (`-A:dev:test` or equivalent from your IDE) and evaluate forms with `clj-nrepl-eval -p <port> "<code>"`. Discover a running server with `clj-nrepl-eval --discover-ports`. Use `:reload` when requiring namespaces to pick up changes.
- For running the tests affected by your change: start a `poly` shell with `clojure -M:poly` (use `clojure`, not `clj` — `clj`'s `rlwrap` produces noisy warnings in this shell), then run the `test` command at the `$` prompt. With no arguments, `test` only runs brick tests that are directly or indirectly changed, so it's much faster than the full suite. Keep the shell open across edits and re-run `test` after each change instead of restarting the JVM each time.
- Only run `bb check` (`clojure -M:dev:test:+default:check`, the static compile check across all components) right before considering a task complete — it's the full, slower delivery gate, not something to repeat on every edit.

## Before modifying Clojure source files

- Check for syntax and structural issues.
- Prefer interactive evaluation (nREPL or the `poly` shell above) over static analysis whenever possible.

## After making changes

- Verify that the modified code compiles.
- Run the affected tests via the `poly` shell's `test` command.
- Use `clj-kondo` (run via `npx clj-kondo`) to catch linting issues.
- Follow this repo's own Polylith conventions — see `doc/` (e.g. `doc/component.adoc`, `doc/workspace.adoc`, `doc/interface.adoc`) for architecture guidance, since this codebase is the reference implementation.
- Ask if something is unclear.
- Before completing a task, run `bb check` and confirm there are no errors or warnings.

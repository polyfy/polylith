
This example shows a situation where you are in the middle of migrating an old system
to Polylith and needs to have access to part of that code ('migrate-me' in this example)
and includes it by using :local/root.

In the end, everything under 'migrate-me' should end up as components and bases.

We also test the case when we only have a component in test context (test-helper).

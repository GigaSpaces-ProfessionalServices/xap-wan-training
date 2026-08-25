# Build notes: lab04-wan_gateway_filter-solution

Implementation rationale behind this lab's Docker setup. Not needed to run the lab - see the root [README.md](../README.md) for that. This is for anyone extending or maintaining the lab who wants to know why it's built this way.

This lab reuses `lab02-active_active`'s grid-level Docker design (flat network, `BillBuddyGateway` as one shared `PropertyPlaceholderConfigurer`-driven module deployed twice) - see [lab02-active_active/docker/build-notes.md](../../lab02-active_active/docker/build-notes.md) for that rationale. This file covers what's specific to the replication filter.

## Why `BillBuddySpaceUS`/`BillBuddySpaceEMEA` are two separate modules, not one shared one

`BillBuddySpaceUS` carries the replication filter bean (`<os-core:space-replication-filter>`); `BillBuddySpaceEMEA` does not. This isn't just a value difference like `lab02-active_active`'s shared, `-p`-overridden `BillBuddySpace` - it's a structural one, an entire extra bean and element present only on one side - so these are two separate modules instead of one shared one, the same precedent as `lab01-active_passive`'s `BillBuddySpaceUS`/`BillBuddySpaceEMEA` split.

Since each module is deployed exactly once (never twice with different `-p` overrides, unlike `BillBuddyGateway`), their `pu.xml` files have no `PropertyPlaceholderConfigurer` at all - `space-name` and `gateway-targets` are hardcoded per module.

## `ReplicationFilter`: a plain module bundled only into `BillBuddySpaceUS`

`ReplicationFilter` is a normal Maven module - it depends on `BillBuddyModel` and compiles against `com.j_spaces.core.cluster.IReplicationFilter`, supplied transitively by `xap-openspaces`'s own dependency on `xap-datagrid`, so no extra dependency declaration is needed. It has no `pu.xml`/`sla.xml` of its own and is never deployed standalone - it's only ever bundled into `BillBuddySpaceUS`.

`BillBuddySpaceUS`'s `assembly.xml` bundles both `BillBuddyModel` and `ReplicationFilter` into `lib/` (the same two-dependencySet exclude/include pattern used everywhere else in this project for bundling a sibling module). `BillBuddySpaceEMEA`'s bundles only `BillBuddyModel`, since it has no filter to include.

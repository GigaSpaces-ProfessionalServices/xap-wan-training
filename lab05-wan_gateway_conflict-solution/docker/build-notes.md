# Build notes: lab05-wan_gateway_conflict-solution

Implementation rationale behind this lab's Docker setup. Not needed to run the lab - see the root [README.md](../README.md) for that. This is for anyone extending or maintaining the lab who wants to know why it's built this way.

This lab reuses `lab02-active_active`'s grid-level Docker design (flat network, `BillBuddySpace`/`BillBuddyGateway` as shared `PropertyPlaceholderConfigurer`-driven modules deployed twice) - see [lab02-active_active/docker/build-notes.md](../../lab02-active_active/docker/build-notes.md) for that rationale. This file covers what's specific to conflict resolution.

## Why the space and gateway deploy steps are split into separate deployers

Every other lab in this project uses one deployer service per site that deploys both the space and gateway PUs together. This lab splits them into separate space and gateway deployers per site, with the gateway deployers profile-gated behind `"gateway"`. This is what makes the conflict demonstration deterministic instead of a timing race: a space PU's outbound gateway target starts redo-logging the instant it's deployed, independent of whether a gateway PU exists yet to drain it. Deploying only the space PUs first, feeding both sites independently while no gateway link exists, then deploying the gateway PUs afterward guarantees every one of the 20 `User` IDs has already diverged on both sides before the gateway connects and drains each side's redo log into the other - producing a real conflict for all 20, every time, rather than one that only sometimes happens depending on feed timing.

## `ConflictResolution`: a dropped unused dependency

`ConflictResolution`'s declared dependency on `BillBuddyModel` in the original (pre-Docker) pom was genuinely unused - `UserConflictHandler` only touches XAP's own conflict-resolution classes, nothing from the model - so it was dropped during modernization.

## Why `BillBuddyGateway` needs an assembly, unlike the other labs' gateway modules

Every other lab's gateway module has no assembly plugin at all, since its configuration never references any bundled sibling module's classes. This lab's `BillBuddyGateway` is the exception: it needs an assembly specifically to bundle `ConflictResolution`'s compiled class into `lib/`. `BillBuddyModel` stays a declared dependency purely for consistency with the other labs' gateway modules, but is never functionally needed here and is excluded from bundling.

# Build notes: lab10-active_active_on_nodes

Implementation rationale and investigation notes for this lab's blueprint. Not needed to run the lab - see the root [README.md](README.md) for that. This is for anyone extending or maintaining the lab who wants the detail behind a design decision or a debugging trail.

## Why these specific `values.yaml` fields exist

- **`gateway.discoveryPort`/`gateway.communicationPort`.** Each node runs a space-side manager+GSCs *and* a separate gateway agent as distinct OS processes; the gateway agent's ports must differ from the space side's defaults, or the two collide on the same node. These two fields are used consistently in both the start-grid script (how the gateway agent is actually launched) and the deploy script (`-p localLookupPort`/`-p localCommunicationPort`) - previously two separate places a value could drift apart.
- **`siteA.host`/`siteB.host`.** Each defined once and substituted everywhere they're needed on both nodes, instead of one side's host address differing between its own scripts and the other side's.
- **`siteA.gatewayName`/`siteB.gatewayName` and `siteA.spaceName`/`siteB.spaceName`.** Likewise defined once, so `remoteGatewayName` on one node is always exactly what the other node calls itself, and the gateway/space names on each side's configuration can't silently drift out of agreement.

## Feeder connectivity: the `SpaceFinder` investigation

Early in this lab's live 2-node testing, `bin/feed.sh` hit `CannotFindSpaceException` / `FinderException: ... LookupFinder failed to find service`, even though the target space was genuinely up and correctly registered. Diagnosed live: a raw Jini `ServiceRegistrar.lookup()` probe, using the exact same match criteria (`Name`, `State`, service type, lookup group) that `SpaceFinder` builds internally, found the space instantly - but every matched `ServiceItem` in that probe printed `service=null`: the entry *attributes* matched fine, but the actual proxy object was never successfully deserialized.

That grid was running with `-Dcom.gs.smart-externalizable.enabled=false` in `GS_OPTIONS_EXT` (a workaround needed in older XAP versions but not on 17.3.0, since removed from `settings.sh`). The most likely explanation: a server that disables smart-externalizable while the client doesn't produces a serialization protocol mismatch that breaks proxy deserialization specifically - which matches the `service=null` symptom exactly and would explain why `SpaceFinder`'s higher-level path (which needs a working proxy) failed while the raw attribute-only lookup didn't.

After removing that flag, the feeder worked immediately, first try, in both directions, on a freshly deployed grid: fed 50 documents into `wanSpaceUS`, confirmed all 50 replicated to `wanSpaceEMEA`; fed 30 more into `wanSpaceEMEA`, confirmed both sides converged to 80.

Not rigorously re-proven by deliberately re-adding the flag and reproducing the failure, but this fits the evidence far better than earlier ruled-out guesses (lookup timeout, IPv6, `--add-opens` JVM flags, a prior failed deploy attempt). If `CannotFindSpaceException` ever reappears, check `GS_OPTIONS_EXT` for `com.gs.smart-externalizable.enabled=false` before re-diagnosing from scratch.

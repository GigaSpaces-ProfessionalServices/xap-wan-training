# Build notes: lab10-active_active_on_nodes

Implementation rationale behind this lab's blueprint. Not needed to run the lab - see the root [README.md](README.md) for that. This is for anyone extending or maintaining the lab who wants to know why it's built this way.

## Why these specific `values.yaml` fields exist

- **`gateway.discoveryPort`/`gateway.communicationPort`.** Each node runs a space-side manager and GSCs *and* a separate gateway agent as distinct OS processes; the gateway agent's ports must differ from the space side's defaults, or the two collide on the same node. These two fields are used consistently in both the start-grid script (how the gateway agent is actually launched) and the deploy script, instead of the same value living in two places that could drift apart.
- **`siteA.host`/`siteB.host`.** Each defined once and substituted everywhere it's needed on both nodes, instead of one side's host address differing between its own scripts and the other side's.
- **`siteA.gatewayName`/`siteB.gatewayName` and `siteA.spaceName`/`siteB.spaceName`.** Likewise defined once, so the remote gateway name on one node is always exactly what the other node calls itself, and the gateway/space names on each side's configuration can't silently drift out of agreement.

## Known gotcha: feeder connectivity depends on a legacy JVM flag

If `bin/feed.sh` fails with a "cannot find space" error even though the target space is genuinely up and correctly registered, check `GS_OPTIONS_EXT` in `settings.sh` for `-Dcom.gs.smart-externalizable.enabled=false`. That flag was a workaround needed in older XAP versions but isn't needed on 17.3.0; running it on the server side without matching it on the client breaks proxy deserialization for standalone clients specifically (the space is discoverable, but the client can't get a working proxy to it), which is exactly this symptom. Removing the flag resolves it - confirmed on a freshly deployed grid, feeding both sites and seeing them converge correctly in both directions.

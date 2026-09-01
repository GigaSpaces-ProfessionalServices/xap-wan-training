# Build notes: lab01-active_passive

Implementation rationale behind this lab's Docker setup. Not needed to run the lab - see the root [README.md](../README.md) for that. This is for anyone extending or maintaining the lab who wants to know why it's built this way.

## Why a flat network, not segmented

US and EMEA containers only differ by IP naming convention (`172.28.1.x`/`172.28.2.x`) - there's no real network-level isolation between them. This is deliberate: XAP's `GS_NIC_ADDRESS` is JVM-wide, used to both bind and advertise every network export in a process, including the embedded gateway lookup service. A dual-homed gateway GSC needs to be reachable by both its own site's manager and the remote gateway, and a single address can't satisfy both if those sites are on separate, non-routed subnets. Flattening to one shared network removes that constraint entirely. See [lab02-active_active/docker/build-notes.md](../../lab02-active_active/docker/build-notes.md) for the fuller history of this decision.

## Why 4 separate Maven modules instead of 2

Unlike `lab02-active_active` (where `BillBuddySpace`/`BillBuddyGateway` are each one shared module deployed twice with different overrides), this topology's US and EMEA sides are structurally asymmetric: US's space has an outbound gateway target and EMEA's doesn't; US's gateway PU has a delegator and no sink, EMEA's has a sink and no delegator. A single shared module isn't a good fit when one side's bean set has beans the other side doesn't have at all, so this topology uses 4 separate modules instead of the usual 2. Both space modules still deploy at the same partition/backup count, so the split doesn't cost anything in HA.

## Why the space modules need the assembly plugin, and the gateway modules don't

The embedded space needs `BillBuddyModel`'s `User`/`Merchant`/`Payment` classes on its classpath to deserialize the objects the feeder writes, and Maven's default jar packaging doesn't bundle dependency jars - so `BillBuddySpaceUS`/`BillBuddySpaceEMEA` use `maven-assembly-plugin` to bundle `BillBuddyModel` into `lib/`. The gateway modules (`BillBuddyGatewayUS`/`BillBuddyGatewayEMEA`) have no assembly plugin at all: their configuration never references any model classes, so Maven's default jar packaging is sufficient.

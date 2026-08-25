# Build notes: lab01-active_passive

Implementation rationale behind this lab's Docker setup. Not needed to run the lab - see the root [README.md](../README.md) for that. This is for anyone extending or maintaining the lab who wants to know why it's built this way.

## Why a flat network, not segmented

US containers are in the `172.28.1.x` range, EMEA in `172.28.2.x`, purely by naming convention - there's no real network-level isolation between them. This is a deliberate choice, not an oversight: XAP's `GS_NIC_ADDRESS` is JVM-wide, used to both bind and advertise every LRMI export in a process, including the embedded gateway LUS. A dual-homed gateway GSC would need to be reachable by both its own site's manager and the remote gateway, and a single address can't satisfy both if those live on separate, non-routed subnets. Flattening to one shared network removes that constraint entirely. See `lab02-active_active/docker/README.md`'s "Why flat, not segmented" section for the fuller history of this decision.

## Why 4 separate Maven modules instead of 2

Unlike `lab02-active_active` (where `BillBuddySpace`/`BillBuddyGateway` are each a single module deployed twice with different `-p` overrides against a symmetric, `PropertyPlaceholderConfigurer`-driven `pu.xml`), this topology's US and EMEA sides are structurally asymmetric: US's space has `gateway-targets` and EMEA's doesn't; US's gateway PU has a `delegator` and no `sink`, EMEA's has a `sink` and no `delegator`. A single shared, parameterized module isn't a good fit for that - one side's bean set has beans the other side doesn't have at all - so this topology uses 4 separate modules instead of the usual 2.

Both `BillBuddySpaceUS` and `BillBuddySpaceEMEA` still deploy at `partitioned 2,1` (2 partitions, 1 backup each), so the module split doesn't cost anything in HA.

## Why the space modules need the assembly plugin, and the gateway modules don't

Both space and gateway modules deploy as plain `target/<Module>.jar` files, but only the two space modules (`BillBuddySpaceUS`/`BillBuddySpaceEMEA`) use `maven-assembly-plugin` (`src/main/assembly/assembly.xml`) to build theirs. The embedded space needs `BillBuddyModel`'s `User`/`Merchant`/`Payment` classes on its classpath to deserialize objects the feeder writes, and Maven's default jar packaging doesn't bundle dependency jars - so the assembly descriptor's 2-dependencySet split (exclude `BillBuddyModel` from the general dependency set, include it in its own) puts `BillBuddyModel`'s classes into the jar's `lib/` directory.

The two gateway modules (`BillBuddyGatewayUS`/`BillBuddyGatewayEMEA`) have no assembly plugin at all: their `pu.xml` files never reference any `BillBuddyModel` classes, so Maven's default jar packaging (which already bundles `META-INF/spring/pu.xml` from `src/main/resources`) is sufficient.

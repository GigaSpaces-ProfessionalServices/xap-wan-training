# Build notes: lab02-active_active

Implementation rationale behind this lab's Docker setup. Not needed to run the lab - see the root [README.md](../README.md) for that. This is for anyone extending or maintaining the lab who wants to know why it's built this way.

## Why a flat network, not segmented

Segmenting the network per site - joined only by the two gateway GSCs, so only gateway traffic crosses sites - is closer to how a production WAN Gateway deployment usually separates its sites. In this Docker lab environment specifically, that runs into a real constraint: XAP's `GS_NIC_ADDRESS` is JVM-wide - one address used to both bind and advertise every network export in that process, including the embedded gateway lookup service. A dual-homed gateway GSC needs to be reachable by both its own site's manager and the remote gateway; with those on disjoint, non-routed subnets, a single address can't satisfy both. Flattening the network to one shared subnet (the Docker-Compose equivalent of VPC peering between two cloud subnets) removes that constraint entirely for the purposes of this lab.

## Why one shared module deployed twice, not a module per site

Both `BillBuddySpace` and `BillBuddyGateway` are real Maven modules, each deployed twice - once per site - against the same built jar, with different `-p` overrides supplying everything site-specific. This works because the two sides are otherwise symmetric, unlike `lab01-active_passive`, where US and EMEA's PUs have genuinely different bean sets and need 4 separate modules instead.

Each module's `pu.xml` uses `PropertyPlaceholderConfigurer`:

- `BillBuddySpace`: `localSpaceName`, `localGatewayName`, `remoteGatewayName`
  ```xml
  <os-core:embedded-space id="space" space-name="${localSpaceName}" mirrored="false" gateway-targets="gatewayTargets" />
  <os-gateway:targets id="gatewayTargets" local-gateway-name="${localGatewayName}">
      <os-gateway:target name="${remoteGatewayName}" />
  </os-gateway:targets>
  ```
- `BillBuddyGateway`: `localGatewayName`, `remoteGatewayName`, `localSpaceUrl`, `localLookupHost/Port`, `remoteLookupHost/Port`, `*CommunicationPort`
  ```xml
  <os-gateway:delegator local-gateway-name="${localGatewayName}" ...>
      <os-gateway:delegation target="${remoteGatewayName}"/>
  </os-gateway:delegator>
  <os-gateway:sink local-gateway-name="${localGatewayName}" local-space-url="${localSpaceUrl}" ...>
  <os-gateway:lookups>
      <os-gateway:lookup gateway-name="${localGatewayName}" host="${localLookupHost}" .../>
      <os-gateway:lookup gateway-name="${remoteGatewayName}" host="${remoteLookupHost}" .../>
  </os-gateway:lookups>
  ```

## Packaging: `BillBuddySpace` vs. `BillBuddyGateway`

`BillBuddySpace`'s `pom.xml` depends on `BillBuddyModel`; `maven-assembly-plugin` bundles `src/main/resources/META-INF/spring/*.xml` plus `BillBuddyModel` into a deployable `target/BillBuddySpace.jar` - the embedded space needs those classes on its classpath to deserialize `User`/`Merchant`/`Payment`. `BillBuddyGateway` has no assembly plugin at all: its `pu.xml` never references any `BillBuddyModel` classes, so Maven's default jar packaging (which already bundles `META-INF/spring/pu.xml` from `src/main/resources`) is sufficient.

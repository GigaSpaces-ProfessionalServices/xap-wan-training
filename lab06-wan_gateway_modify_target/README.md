# lab06-wan_gateway_modify_target - Adding and Removing Gateway Targets

An operator needs to pause replication toward a site that's about to go down for planned maintenance, so writes stop queuing up against a target that won't be there to receive them - then resume it once the site is back, without ever touching the space's deployment. WAN gateway targets can be added to or removed from a running space at any time through the Admin API, entirely independent of redeploying the space or reconfiguring the gateway itself.

## Lab Goals

- Get familiar with adding and removing outbound WAN gateway targets at runtime, via the Admin API.
- Confirm that a fully connected gateway (delegator + sink, `CONNECTED` state) is necessary but not sufficient for replication - the space also needs an active target.
- Add a target while the space is live, confirm replication starts; remove it again, confirm replication stops - all without redeploying anything.

## Lab Description

Both sites deploy with their gateway (delegator + sink) fully connected to each other from the start, exactly like `lab02-active_active`. Unlike every other lab in this set, though, both spaces deploy with an **empty** gateway-target list - so out of the box, neither side replicates anywhere, even though the gateway link between them is already up. A `ModifyGatewayTargetInitiator` admin client (modeled on the reference `ModifyTarget.java` tool from GigaSpaces' own WAN gateway examples) adds `EMEA` as an outbound target on `wanSpaceUS`, data written afterward replicates across, and then the same client removes that target again, after which further writes stay local to US. There is no exercise variant of this lab - only this solution.

## Prerequisites

- Docker and Docker Compose v2
- The `gigaspaces/smart-cache-enterprise:17.3.0` image
- Maven, to build the reactor's modules

## Topology

```
 wan-net (172.28.0.0/16), one flat network
 ┌──────────────────────────────┐    ┌─────────────────────────────────┐
 │ us-manager    172.28.1.10    │    │ emea-manager   172.28.2.10      │
 │ us-space-gsc  172.28.1.21    │    │ emea-space-gsc 172.28.2.21      │
 │ us-gateway-gsc 172.28.1.30 ──┼────┼── 172.28.2.30  emea-gateway-gsc │
 │  gateway: CONNECTED          │    │  gateway: CONNECTED             │
 │  wanSpaceUS targets: []      │    │  wanSpaceEMEA targets: []       │
 └──────────────────────────────┘    └─────────────────────────────────┘
```

Both gateways run a full delegator and sink and connect to each other immediately on startup - that part never changes in this lab. What changes, live, is `wanSpaceUS`'s own outbound target list, toggled through the Admin API. `wanSpaceEMEA`'s target list is also empty but is never touched - this lab only ever demonstrates the US-to-EMEA direction.

## Module structure

- `BillBuddyModel` - shared domain classes (`User`, `Merchant`, `Payment`) used by every other module.
- `BillBuddyAccountFeeder` - a standalone client that writes one phase of data (users, merchants, or payments) into a space, selected via a `FEED_SET` argument - see [docker/build-notes.md](docker/build-notes.md) for why.
- `BillBuddySpace` - the space module, deployed once per site with `-p` overrides for the site-specific space name; both deployments get an empty gateway-target list.
- `BillBuddyGateway` - the WAN gateway module (delegator and sink), deployed once per site. Configured via `@Configuration`/`@Bean` Java classes (`ServiceConfig`/`GatewayBeansConfig`) rather than the `os-gateway:*` XML every other lab uses - see [docker/build-notes.md](docker/build-notes.md) for why, and for the `pu-type=gateway` reasoning.
- `ModifyGatewayTarget` - a one-shot admin client (`ModifyGatewayTargetInitiator`), not a Processing Unit. Adds or removes a named gateway target on a running space's replication manager, selected via `ACTION=add`/`ACTION=remove`.

All five live in this lab's own standalone Maven reactor. `BillBuddyModel` and `BillBuddyAccountFeeder` are duplicated here rather than shared with the other labs in this training set, matching how each one is independently self-contained.

## Build and run it

```bash
# 1. Build the reactor
mvn install

# 2. Bring up both sites - order doesn't matter here, unlike lab03
cd docker
docker compose up -d
docker compose logs -f us-deployer emea-deployer   # wait for both "deployment complete."

# 3. Feed US with users, while it has no gateway target configured
docker compose --profile feeder run --rm -e FEED_SET=users us-feeder

# 4. Add the EMEA target to wanSpaceUS
docker compose --profile admin run --rm -e ACTION=add modify-target

# 5. Feed US with merchants, now that the target is active
docker compose --profile feeder run --rm -e FEED_SET=merchants us-feeder

# 6. Remove the EMEA target from wanSpaceUS again
docker compose --profile admin run --rm -e ACTION=remove modify-target

# 7. Feed US with payments, after the target has been removed again
docker compose --profile feeder run --rm -e FEED_SET=payments us-feeder
```

## Verify add/remove took effect

Check `wanSpaceEMEA`'s per-type counts after each feed step:

```bash
docker run --rm --network wan-modify-target_wan-net --entrypoint /bin/bash \
  gigaspaces/smart-cache-enterprise:17.3.0 -c \
  "/opt/gigaspaces/bin/gs.sh --server=emea-manager space info --type-stats wanSpaceEMEA"
```

- After step 3 (users, no target yet): `wanSpaceEMEA` has 0 objects of any type, even though the gateway link is `CONNECTED` - confirming that connectivity alone doesn't replicate anything.
- After step 5 (merchants, target added in step 4): `wanSpaceEMEA` has the 16 Merchant objects, but still 0 Users - the Users written in step 3, before the target existed, were never retroactively pushed. Only writes made *after* a target is added replicate through it.
- After step 7 (payments, target removed in step 6): `wanSpaceEMEA`'s counts are unchanged from step 5 - still 16 Merchants, still 0 Users, and 0 Payments, confirming the ~499 Payment objects written to `wanSpaceUS` in step 7 stayed local once the target was removed.

You can also confirm `wanSpaceUS` itself has all three types at each point (`gs.sh --server=us-manager space info --type-stats wanSpaceUS`) - the local writes always succeed regardless of the target's state; it's only the EMEA side that reflects whether a target was active at write time.

- US manager REST V3 API / Swagger UI: http://localhost:19090/api/v3/swagger-ui/index.html
- EMEA manager REST V3 API / Swagger UI: http://localhost:29090/api/v3/swagger-ui/index.html

## Tear down

```bash
docker compose --profile feeder --profile admin down -v
```

## Notes

See [docker/build-notes.md](docker/build-notes.md) for the implementation rationale behind this lab's Docker setup: why both spaces deploy with an empty gateway-target list (confirmed schema-valid against the real `openspaces-gateway.xsd`), how `ModifyGatewayTarget` ports the reference `ModifyTarget.java` tool's Admin API calls into this project's conventions, and why `AccountFeeder` gained a feed-set argument. This lab also reuses `lab02-active_active`'s grid-level design (flat network, shared modules deployed twice); see [lab02-active_active/docker/build-notes.md](../lab02-active_active/docker/build-notes.md) for that.

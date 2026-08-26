# lab02-active_active - WAN Gateway Basics

For applications that need to serve users in multiple regions at once, active-active WAN replication keeps both sites live and writable, so end users can read and write against a geographically local cluster and experience low latency.

This lab covers the WAN Gateway active-active topology: two sites, US and EMEA, each fully able to accept writes and replicate them out to the other. The whole grid runs as Docker containers. This is a solution lab, so everything described below is already implemented and working.

## Lab Goals

- Get familiar with the WAN Gateway active-active topology.
- Deploy the topology in Docker, feed data into either site, and confirm it replicates to the other.

## Lab Description

Both US and EMEA are peers: each has its own gateway delegator and sink, so writes made at either site replicate to the other. This is bidirectional replication, unlike `lab01-active_passive`'s one-directional setup, where only US delegates and only EMEA sinks.

## Prerequisites

- Docker and Docker Compose v2
- The `gigaspaces/smart-cache-enterprise:17.3.0` image
- Maven, to build the reactor's modules

## Topology

Six containers total, three per site: a manager, a space-GSC container running all four space GSCs (2 partitions x 1 backup), and a dedicated gateway-GSC.

```
 wan-net (172.28.0.0/16), one flat network
 ┌──────────────────────────────┐    ┌─────────────────────────────────┐
 │ us-manager     172.28.1.10   │    │ emea-manager   172.28.2.10      │
 │ us-space-gsc   172.28.1.21   │    │ emea-space-gsc 172.28.2.21      │
 │ us-gateway-gsc 172.28.1.30 ──┼────┼── 172.28.2.30  emea-gateway-gsc │
 └──────────────────────────────┘    └─────────────────────────────────┘
```

Both gateway GSCs run a delegator and a sink, unlike `lab01-active_passive` where one side only delegates and the other only sinks. Because the two sides are otherwise symmetric, the space and gateway roles are each one shared module deployed twice (once per site) with different `-p` overrides, rather than a dedicated module per site. See the Notes section below for the implementation rationale.

## Module structure

- `BillBuddyModel` - shared domain classes (`User`, `Merchant`, `Payment`) used by every other module.
- `BillBuddyAccountFeeder` - a standalone client that writes users, merchants, and payments into a space.
- `BillBuddySpace` - the space module, deployed once per site with `-p` overrides for the site-specific space name and gateway targets.
- `BillBuddyGateway` - the WAN gateway module (delegator and sink), deployed once per site with `-p` overrides for the site-specific gateway name, lookup hosts, and ports.

All four live in this lab's own standalone Maven reactor. `BillBuddyModel` and `BillBuddyAccountFeeder` are duplicated here rather than shared with `lab01-active_passive`, matching how each lab in this training set is independently self-contained.

## Build and run it

```bash
# 1. Build the reactor
mvn install

# 2. Bring up both sites' grids and deploy the space/gateway PUs
cd docker
docker compose up -d
docker compose logs -f us-deployer emea-deployer

# 3. Feed data into either site and watch it replicate to the other
docker compose --profile feeder run --rm us-feeder
docker compose --profile feeder run --rm emea-feeder   # or the reverse direction too
```

## Verify replication

```bash
docker run --rm --network docker_wan-net --entrypoint /bin/bash \
  gigaspaces/smart-cache-enterprise:17.3.0 -c \
  "/opt/gigaspaces/bin/gs.sh --server=us-manager space query wanSpaceUS \
   com.gigaspaces.training.billbuddy.model.Payment --max-results=2000 --columns=paymentId"
```

Both sides should converge to the same record counts regardless of which site was fed. Unlike `lab01-active_passive`, there is both a `us-feeder` and an `emea-feeder`: replication works in both directions.

- US manager REST V3 API / Swagger UI: http://localhost:19090/api/v3/swagger-ui/index.html
- EMEA manager REST V3 API / Swagger UI: http://localhost:29090/api/v3/swagger-ui/index.html

These ports collide with `lab01-active_passive`'s if both stacks run at the same time; bring up only one topology at a time, or remap ports in one of the two `docker-compose.yaml` files.

## Tear down

```bash
docker compose down -v
```

## Notes

See [docker/build-notes.md](docker/build-notes.md) for the implementation rationale behind this lab's Docker setup: why the network is flat rather than segmented, why the space and gateway roles are each one shared module deployed twice instead of a dedicated module per site, and how each one gets packaged for Docker.

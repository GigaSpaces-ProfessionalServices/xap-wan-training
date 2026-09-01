# Build notes: lab06-wan_gateway_modify_target

Implementation rationale behind this lab's Docker setup. Not needed to run the lab - see the root [README.md](../README.md) for that. This is for anyone extending or maintaining the lab who wants to know why it's built this way.

This lab reuses `lab02-active_active`'s grid-level Docker design (flat network, one shared `PropertyPlaceholderConfigurer`-driven module per PU type deployed twice) - see [lab02-active_active/docker/build-notes.md](../../lab02-active_active/docker/build-notes.md) for that rationale. This file covers what's specific to modifying gateway targets at runtime.

## Why both spaces deploy with an empty gateway-target list

Unlike every other lab in this set, `BillBuddySpace/pu.xml`'s gateway-target list has zero entries on **both** US and EMEA - both sides are identical, no per-site override needed. An empty list is valid, schema-checked config (not a workaround), and it still makes the space gateway-enabled: `addGatewayTarget`/`removeGatewayTarget` work against it from the moment it's deployed, it just starts with nothing in it.

That list being empty is deliberate: `BillBuddyGateway`'s delegator and sink are still fully deployed and connect to each other immediately (same as `lab02-active_active`/`lab03-wan_bootstrap-solution`), so the gateway shows `CONNECTED` from the start - but that's necessary and not sufficient for replication, since the *space* also needs an active target. This lab is the one place in this training set that isolates that distinction and lets you toggle it live.

## `ModifyGatewayTarget`: a one-shot client, not a Processing Unit

Structured the same way as `lab03-wan_bootstrap-solution/AdminBootStrap`: no `sla.xml`, not deployed to a GSC, built like `BillBuddyAccountFeeder` (a self-contained uber jar in a plain `eclipse-temurin:17-jre` container).

`ModifyGatewayTargetInitiator.java` ports the real Admin API calls from GigaSpaces' own reference WAN gateway examples (`space.getReplicationManager().addGatewayTarget(new GatewayTarget(name), true)` / `.removeGatewayTarget(name)`) into this project's env-var-driven, one-shot style (`LOCATORS`/`SPACE_NAME`/`GATEWAY_NAME`/`ACTION`), rather than the source tool's interactive CLI-flag parser - a lab step here is meant to be one deterministic `docker compose run` invocation. The `true` argument to `addGatewayTarget` relaxes a sequential-packet-numbering check that would otherwise reject re-adding a target that was previously removed - exactly the add/remove/add-again pattern this lab exercises.

## Why `AccountFeeder` gained a feed-set argument

`UserFeeder`/`MerchantFeeder`/`PaymentFeeder` are each idempotent by id - re-running the same feeder against a space that already has that data is a no-op, and a newly-added gateway target never retroactively pushes a space's *existing* data (that's what bootstrapping, see `lab03-wan_bootstrap-solution`, is a separate mechanism for). To make "add" and "remove" each visibly do something, this lab's copy of `AccountFeeder` takes an optional feed-set argument (`users|merchants|payments|all`) so each lab step can write one distinguishable phase of data: users before any target exists, merchants once the target is added, payments once it's removed again. This is a small, additive change scoped to this lab's own copy of the module - `lab03-wan_bootstrap-solution`'s feeder is untouched.

## `BillBuddyGateway`: annotation-based (`@Configuration`) instead of XML/XSD

Unlike every other lab in this training set (and unlike `BillBuddySpace` in this same lab), `BillBuddyGateway` is no longer configured through `os-gateway:*` XML. `pu.xml` is reduced to the same minimal shape GigaSpaces' own `my-pu-stateless`/`my-stateful-with-db` reference projects use - a `pu-type` description, annotation support, and one bean pointing at a `ServiceConfig` class. All the delegator/sink/lookups wiring moved into `GatewayBeansConfig`, a plain `@Configuration` class with `@Bean` factory methods (there's no framework base class for gateway PUs the way `EmbeddedSpaceBeansConfig` exists for space PUs, so this builds `GatewayDelegatorFactoryBean`/`GatewaySinkFactoryBean`/`GatewayLookupsFactoryBean` directly).

`pu-type=gateway` is used in the description, even though it's not one of GigaSpaces' three built-in archetypes (`stateless`/`stateful`/`mirror` are the only values that appear anywhere in the product). This is a documentation-only convention read by external tooling, not something the GigaSpaces container itself validates or enforces, so a new descriptive value for a PU that's genuinely none of the three existing archetypes is safe.

This conversion needed a new compile dependency, `com.gigaspaces:xap-admin` (scoped `provided`), that the XML version never declared - the delegator/sink classes live there, and annotation config needs them at compile time rather than only at deploy time. `provided` (not `compile`) because this module deploys *into* a GSC that already supplies those classes at runtime; bundling them would risk a duplicate copy on the classpath.

Fully validated live with the same real `mvn install` + `docker compose up` cycle as the XML version: both gateways reach `CONNECTED`, and a full add-target/feed/remove-target/feed cycle produces the same replication result as before - confirming this is purely a change in how the beans get declared, not a behavior change.

Only `BillBuddyGateway` was converted. `BillBuddySpace` (this lab's other pu.xml, with the gateway-target list) is still XML.

## Why the compose file sets an explicit project name

`docker-compose.yaml` sets `name: wan-modify-target` at the top level. Without it, Compose derives the project name from the containing directory's basename (`docker`) - several labs in this training set have their compose file living in a directory literally named `docker/`, which would collide if two were ever brought up concurrently without tearing one down first.
